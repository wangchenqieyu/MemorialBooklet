package contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class CidStorage extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b506103358061001c5f395ff3fe608060405234801561000f575f5ffd5b5060043610610034575f3560e01c8063a7005a1c14610038578063ccfdd32b14610056575b5f5ffd5b61004061006b565b60405161004d919061010b565b60405180910390f35b610069610064366004610140565b6100fa565b005b60605f8054610079906101ae565b80601f01602080910402602001604051908101604052809291908181526020018280546100a5906101ae565b80156100f05780601f106100c7576101008083540402835291602001916100f0565b820191905f5260205f20905b8154815290600101906020018083116100d357829003601f168201915b5050505050905090565b5f610106828483610245565b505050565b602081525f82518060208401528060208501604085015e5f604082850101526040601f19601f83011684010191505092915050565b5f5f60208385031215610151575f5ffd5b823567ffffffffffffffff811115610167575f5ffd5b8301601f81018513610177575f5ffd5b803567ffffffffffffffff81111561018d575f5ffd5b85602082840101111561019e575f5ffd5b6020919091019590945092505050565b600181811c908216806101c257607f821691505b6020821081036101e057634e487b7160e01b5f52602260045260245ffd5b50919050565b634e487b7160e01b5f52604160045260245ffd5b601f82111561010657805f5260205f20601f840160051c8101602085101561021f5750805b601f840160051c820191505b8181101561023e575f815560010161022b565b5050505050565b67ffffffffffffffff83111561025d5761025d6101e6565b6102718361026b83546101ae565b836101fa565b5f601f8411600181146102a2575f851561028b5750838201355b5f19600387901b1c1916600186901b17835561023e565b5f83815260208120601f198716915b828110156102d157868501358255602094850194600190920191016102b1565b50868210156102ed575f1960f88860031b161c19848701351681555b505060018560011b018355505050505056fea2646970667358221220b48513aef164528db7deba2bb0c6d372777d651dd4de7a44653c8987f4f5593a64736f6c634300081e0033";

    public static final String FUNC_GETMYCID = "getMyCID";

    public static final String FUNC_STORECID = "storeCID";

    @Deprecated
    protected CidStorage(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CidStorage(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CidStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CidStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> getMyCID() {
        final Function function = new Function(FUNC_GETMYCID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> storeCID(String cid) {
        final Function function = new Function(
                FUNC_STORECID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(cid)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static CidStorage load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CidStorage(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CidStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CidStorage(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CidStorage load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CidStorage(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CidStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CidStorage(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CidStorage> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CidStorage.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CidStorage> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CidStorage.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<CidStorage> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CidStorage.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CidStorage> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CidStorage.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
