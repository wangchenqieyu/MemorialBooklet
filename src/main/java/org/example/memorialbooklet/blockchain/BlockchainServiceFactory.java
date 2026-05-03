package org.example.memorialbooklet.blockchain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 区块链服务工厂：负责创建BlockchainService实例，呵呵
 * 后续如需切换到其他链（如ETH测试网），只需新增实现类，无需修改上层代码
 */
@Component
public class BlockchainServiceFactory {

    // 触发de（Spring自动扫描Bean）
    @Autowired
    private ConfluxBlockchainServiceImpl confluxService;

    /**
     * 获取区块链服务实例
     * @param chainType 链类型：CONFLUX（Conflux）、ETH（后续扩展）
     * @return BlockchainService接口实例
     */
    public BlockchainService getBlockchainService(String chainType) {
        switch (chainType.toUpperCase()) {
            case "CONFLUX":
                return confluxService;
            // 后续扩展ETH时，新增case：case "ETH": return ethService;
            default:
                throw new IllegalArgumentException("不支持的区块链类型：" + chainType);
        }
    }
}
