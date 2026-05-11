package org.example.memorialbooklet.blockchain;

import org.example.memorialbooklet.contract.CidStorage;
import org.example.memorialbooklet.mapper.DigitalLegacyMapper;
import org.example.memorialbooklet.pedigree.mybatis.type.DigitalLegacyAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * Conflux区块链实现：连接Conflux测试网，调用CID存储合约
 */
@Service
public class ConfluxBlockchainServiceImpl implements BlockchainService {

    private static final Logger log = LoggerFactory.getLogger(ConfluxBlockchainServiceImpl.class);

    @Value("${conflux.rpc.url}")
    private String confluxRpcUrl; // 配置：https://evmtestnet.confluxrpc.com

    @Value("${conflux.private.key:}")
    private String privateKey; // 测试网账户私钥

    @Value("${conflux.contract.address:}")
    private String contractAddress; // 部署的CID存储合约地址

    @Autowired
    private DigitalLegacyMapper legacyMapper;

    private CidStorage contract; // 合约交互实例
    private volatile boolean confluxEnabled;

    // 初始化区块链客户端和合约（服务启动时执行）
    @Override
    @javax.annotation.PostConstruct
    public void initContract() {
        if (!StringUtils.hasText(privateKey) || !StringUtils.hasText(contractAddress)) {
            confluxEnabled = false;
            log.warn("Conflux is disabled because conflux.private.key or conflux.contract.address is empty. "
                    + "Application startup continues, but chain APIs will return an explicit error until configured.");
            return;
        }

        Web3j web3j = Web3j.build(new HttpService(confluxRpcUrl));
        Credentials credentials = Credentials.create(privateKey.trim());
        this.contract = CidStorage.load(
                contractAddress.trim(),
                web3j,
                credentials,
                new ConfluxTestnetGasProvider()
        );
        confluxEnabled = true;
    }

    private void ensureConfluxEnabled() {
        if (!confluxEnabled || contract == null) {
            throw new IllegalStateException("Conflux is not configured. Please provide CONFLUX_PRIVATE_KEY and CONFLUX_CONTRACT_ADDRESS.");
        }
    }

    @Override
    public String storeCid(String cid, Long personId) {
        try {
            ensureConfluxEnabled();
            // 调用合约storeCID方法，返回交易哈希
            String contractHashCode = contract.storeCID(cid).send().getTransactionHash();

            DigitalLegacyAsset asset = new DigitalLegacyAsset();
            asset.setPersonId(personId);
            asset.setConfluxCode(contractHashCode);
            // 回填 Conflux 存证哈希到数据库
            legacyMapper.updateConfluxCode(asset.getId(), contractHashCode);
            return contractHashCode;
        } catch (Exception e) {
            throw new RuntimeException("Conflux上链失败", e);
        }
    }

    @Override
    public String queryCid() {
        try {
            ensureConfluxEnabled();
            return contract.getMyCID().send();
        } catch (Exception e) {
            throw new RuntimeException("Conflux查询失败", e);
        }
    }
}