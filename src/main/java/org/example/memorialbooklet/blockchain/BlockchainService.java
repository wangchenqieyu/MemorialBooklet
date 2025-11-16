package org.example.memorialbooklet.blockchain;

public interface BlockchainService {
    void initContract() throws Exception;
    String storeCid(String cid);
    String queryCid();
}
