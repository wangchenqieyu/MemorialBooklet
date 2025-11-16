package org.example.memorialbooklet.blockchain;

import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

public class ConfluxTestnetGasProvider extends StaticGasProvider {
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(30_000_000_000L);

    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(300_000L);

    public ConfluxTestnetGasProvider() {
        super(GAS_PRICE, GAS_LIMIT);
    }
}
