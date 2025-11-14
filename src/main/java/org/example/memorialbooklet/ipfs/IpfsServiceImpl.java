package org.example.memorialbooklet.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class IpfsServiceImpl implements IpfsService {

    private final IPFS ipfsClient;

    @Autowired // 自动注入 Factory
    public IpfsServiceImpl(IpfsClientFactory factory) {
        this.ipfsClient = factory.createIPFSClient();
    }

    /**
     * 实现上传文件流到 IPFS
     */
    @Override
    public String uploadFile(InputStream inputStream) throws IOException {
        // NamedStreamable.InputStreamWrapper 允许我们直接使用 InputStream
        NamedStreamable.InputStreamWrapper file = new NamedStreamable.InputStreamWrapper(inputStream);

        // 使用 ipfs.add 方法上传文件
        // add 方法返回 MerkleNode 列表
        List<MerkleNode> results = ipfsClient.add(file);

        if (results.isEmpty()) {
            throw new IOException("IPFS 添加文件失败，未返回 MerkleNode。");
        }

        // 返回第一个（通常也是唯一一个）文件的 hash，即 CID
        // toString() 方法将 Multihash 对象转换为 Base58 编码的 CID 字符串
        return results.getFirst().hash.toString();
    }
}