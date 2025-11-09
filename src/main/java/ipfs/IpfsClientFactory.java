package ipfs;

import io.ipfs.api.IPFS;
import java.io.IOException;

// 工厂类：用于创建和管理 IPFS 客户端实例
public class IpfsClientFactory {

    // 假设 IPFS 节点的 Multiaddr 地址
    private static final String DEFAULT_IPFS_ADDR = "/ip4/127.0.0.1/tcp/5001";

    private final String ipfsMultiAddress;

    // 构造函数，接受配置的 IPFS 地址
    public IpfsClientFactory(String ipfsMultiAddress) {
        this.ipfsMultiAddress = ipfsMultiAddress != null ? ipfsMultiAddress : DEFAULT_IPFS_ADDR;
    }

    // 工厂方法：创建 IPFS 客户端实例
    public IPFS createIPFSClient() {
        // IPFS 客户端通过 Multiaddr 地址连接 IPFS 节点
        return new IPFS(ipfsMultiAddress);
    }
}