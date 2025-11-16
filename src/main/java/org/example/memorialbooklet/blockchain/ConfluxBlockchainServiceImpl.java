package org.example.memorialbooklet.blockchain;

import org.example.memorialbooklet.contract.CidStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import conflux.web3j.Cfx;

/**
 * Conflux区块链实现：连接Conflux测试网，调用CID存储合约
 */
@Service
public class ConfluxBlockchainServiceImpl implements BlockchainService {

    @Value("${conflux.rpc.url}")
    private String confluxRpcUrl; // 配置：https://evmtestnet.confluxrpc.com

    @Value("${conflux.private.key}")
    private String privateKey; // 测试网账户私钥

    @Value("${conflux.contract.address}")
    private String contractAddress; // 部署的CID存储合约地址

    private CidStorage contract; // 合约交互实例

    // 初始化区块链客户端和合约（服务启动时执行）
    @Override
    @javax.annotation.PostConstruct
    public void initContract() throws Exception {

        Web3j web3j = Web3j.build(new HttpService(confluxRpcUrl));

        Credentials credentials = Credentials.create(privateKey);
        this.contract = CidStorage.load(
                contractAddress,
                web3j,
                credentials,
                new ConfluxTestnetGasProvider()
        );
    }

    @Override
    public String storeCid(String cid) {
        try {
            // 调用合约storeCID方法，返回交易哈希
            return contract.storeCID(cid).send().getTransactionHash();
        } catch (Exception e) {
            throw new RuntimeException("Conflux上链失败", e);
        }
    }

    @Override
    public String queryCid() {
        try {
            return contract.getMyCID().send();
        } catch (Exception e) {
            throw new RuntimeException("Conflux查询失败", e);
        }
    }
}