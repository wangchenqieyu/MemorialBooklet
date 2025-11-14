package org.example.memorialbooklet.blockchain;

public interface BlockchainService {
    void afterPropertiesSet() throws Exception;
    String storeCid(String cid);
    String queryCid();
}
