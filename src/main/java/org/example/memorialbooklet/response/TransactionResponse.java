package org.example.memorialbooklet.response;

public class TransactionResponse {
    private final String txHash;
    private final String message;
    private final String chain;

    public TransactionResponse(String txHash, String message, String chain) {
        this.txHash = txHash;
        this.message = message;
        this.chain = chain;
    }

    // Getters
    public String getTxHash() {
        return txHash;
    }

    public String getMessage() {
        return message;
    }

    public String getChain() {
        return chain;
    }
}
