package com.bsiag.ethereum.fxtradingnetwork.events.server;

import java.lang.String;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use {@link org.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version 2.0.2.
 */
public final class OrderBook extends Contract {
    private static final String BINARY = "0x606060405234610000576040516117f13803806117f1833981016040528051015b60058054600160a060020a0319166c01000000000000000000000000338102041790819055600160a060020a031660009081526004602090815260408220805460ff1916600190811790915583516003805494819052937fc2575a0e9e593c00f959f8c92f12db2869c3395a3b0502d05e2516446f71f85b600261010094831615949094026000190190911692909204601f9081018490048301939192918601908390106100d957805160ff1916838001178555610106565b82800160010185558215610106579182015b828111156101065782518255916020019190600101906100eb565b5b506101279291505b80821115610123576000815560010161010f565b5090565b505060016006555b505b6116b28061013f6000396000f3606060405236156100da5760e060020a600035046305a7585b81146100df57806329b948481461010057806335cea2881461011f5780633adbcccb1461016a57806341c0e1b5146101895780634a8393f31461019857806351a742e0146101e357806361915e5d14610202578063668a20011461024d5780637c33e99a1461025f578063824eea671461027e578063929066f5146102a957806395d89b41146102cd578063a8c67f0514610348578063c4ccb40a146103c9578063ca8836d2146103e8578063cf4b6c491461040c578063dfafe10f14610436575b610000565b34610000576100ec610448565b604080519115158252519081900360200190f35b346100005761010d6104da565b60408051918252519081900360200190f35b346100005761012f6004356104e1565b60408051968752602087019590955292151585850152600160a060020a039091166060850152608084015260a0830152519081900360c00190f35b346100005761010d610533565b60408051918252519081900360200190f35b346100005761019661053a565b005b346100005761012f600435610562565b60408051968752602087019590955292151585850152600160a060020a039091166060850152608084015260a0830152519081900360c00190f35b346100005761010d6105b4565b60408051918252519081900360200190f35b346100005761012f6004356105bb565b60408051968752602087019590955292151585850152600160a060020a039091166060850152608084015260a0830152519081900360c00190f35b346100005761019660043561060d565b005b346100005761010d610631565b60408051918252519081900360200190f35b346100005761010d600435602435604435606435610673565b60408051918252519081900360200190f35b34610000576100ec600435610918565b604080519115158252519081900360200190f35b34610000576102da61093a565b60405180806020018281038252838181518152602001915080519060200190808383829060006004602084601f0104600302600f01f150905090810190601f16801561033a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34610000576102da6004356024356109c8565b60405180806020018281038252838181518152602001915080519060200190808383829060006004602084601f0104600302600f01f150905090810190601f16801561033a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b346100005761010d610bc3565b60408051918252519081900360200190f35b34610000576100ec600435610c03565b604080519115158252519081900360200190f35b346100005761041f600435602435610cb3565b6040805160009290920b8252519081900360200190f35b3461000057610196600435610ee2565b005b6000805481908190158061045c5750600154155b1561046a57600092506104d5565b6000805460001981019081101561000057906000526020600020906005020160005b50600180549193509060001981019081101561000057906000526020600020906005020160005b509050806001015482600101541015156104d057600192506104d5565b600092505b505090565b6001545b90565b600081815481101561000057906000526020600020906005020160005b508054600182015460028301546003840154600490940154929450909260ff821692610100909204600160a060020a03169186565b6002545b90565b60055433600160a060020a039081169116141561055f57600554600160a060020a0316ff5b5b565b600181815481101561000057906000526020600020906005020160005b508054600182015460028301546003840154600490940154929450909260ff821692610100909204600160a060020a03169186565b6000545b90565b600281815481101561000057906000526020600020906005020160005b508054600182015460028301546003840154600490940154929450909260ff821692610100909204600160a060020a03169186565b600160a060020a0381166000908152600460205260409020805460ff191690555b50565b600154600090151561064657506000196104de565b6001805460001981019081101561000057906000526020600020906005020160005b506003015490505b90565b6040805160c08101825260008082526020808301829052828401829052606083018290526080830182905260a08301829052600160a060020a0333168252600490529182205460ff1615156106c757610000565b506040805160c081018252868152602081018690529081018490523360608201526006805460018101909155608082015260a08101839052831561080b57600080548060010182818154818355818115116107755760050281600502836000526020600020918201910161077591905b8082111561077157600080825560018201819055600282018054600160a860020a0319169055600382018190556004820155600501610737565b5090565b5b505050916000526020600020906005020160005b5082518155602083015160018083019190915560408401516002830180546060870151606060020a908102046101000261010060a860020a031960f860020a9485029490940460ff1990921691909117929092169190911790556080840151600383015560a08401516004909201919091556108069150610f09565b610906565b6001805480600101828181548183558181151161087b5760050281600502836000526020600020918201910161087b91905b8082111561077157600080825560018201819055600282018054600160a860020a0319169055600382018190556004820155600501610737565b5090565b5b505050916000526020600020906005020160005b50825181556020830151600182015560408301516002820180546060860151606060020a908102046101000261010060a860020a031960f860020a9485029490940460ff1990921691909117929092169190911790556080830151600382015560a0830151600490910155506109066000610f09565b5b806080015191505b50949350505050565b600160a060020a03811660009081526004602052604090205460ff165b919050565b6003805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156109c05780601f10610995576101008083540402835291602001916109c0565b820191906000526020600020905b8154815290600101906020018083116109a357829003601f168201915b505050505081565b604080516020810190915260008082528080846109e65760016109e9565b60005b9150600090505b8154811015610bb957858282815481101561000057906000526020600020906005020160005b50600301541415610a2657600192505b8215610baf57815460001901811015610af0578181600101815481101561000057906000526020600020906005020160005b508282815481101561000057906000526020600020906005020160005b50815481556001808301549082015560028083018054918301805460f860020a60ff90941684029390930460ff19909316929092178083559054606060020a600160a060020a0361010092839004168102040261010060a860020a031990911617905560038083015490820155600491820154910155610baf565b8181815481101561000057906000526020600020906005020160005b50600080825560018201819055600282018054600160a860020a0319169055600382018190556004909101558154600019810180845590839082908015829011610ba957600502816005028360005260206000209182019101610ba991905b8082111561077157600080825560018201819055600282018054600160a860020a0319169055600382018190556004820155600501610737565b5090565b5b505050505b5b5b6001016109f0565b5b50505092915050565b600080541515610bd657506000196104de565b6000805460001981019081101561000057906000526020600020906005020160005b506003015490505b90565b600080808311610c165760009150610cad565b5060005b600054811015610c5f57600081815481101561000057906000526020600020906005020160005b5060030154831415610c565760019150610cad565b5b600101610c1a565b5060005b600154811015610ca857600181815481101561000057906000526020600020906005020160005b5060030154831415610c9f5760019150610cad565b5b600101610c63565b600091505b50919050565b6000600060006000600060008054905060001480610cf757506000805460001981019081101561000057906000526020600020906005020160005b50600301548714155b15610d06576000199450610ed8565b6001541580610d3b57506001805460001981019081101561000057906000526020600020906005020160005b50600301548614155b15610d4a576001199450610ed8565b6001805460001981019081101561000057906000526020600020906005020160005b50600101546000805460001981019081101561000057906000526020600020906005020160005b50600101541015610da8576002199450610ed8565b6000805460001981019081101561000057906000526020600020906005020160005b50600180549195509060001981019081101561000057906000526020600020906005020160005b50805485549194509010610e06578254610e09565b83545b600184810154908601546040805160c08101825288548152602081018390526002808a015460ff8116151593830193909352610100909204600160a060020a0316606082015260038901546080820152600489015460a08201529395509101049150610e769083836113a7565b6040805160c0810182528454815260018501546020820152600285015460ff8116151592820192909252610100909104600160a060020a0316606082015260038401546080820152600484015460a0820152610ed39083836113a7565b600094505b5050505092915050565b600160a060020a0381166000908152600460205260409020805460ff191660011790555b50565b600060c06040519081016040528060008152602001600081526020016000815260200160008152602001600081526020016000815260200150600083156111765750600054915060001982015b600081111561117157600060018203815481101561000057906000526020600020906005020160005b5060010154600082815481101561000057906000526020600020906005020160005b5060010154101561116757600060018203815481101561000057906000526020600020906005020160005b506040805160c0810182528254815260018301546020820152600283015460ff8116151592820192909252610100909104600160a060020a031660608201526003820154608082015260049091015460a08201526000805491935090829081101561000057906000526020600020906005020160005b50600060018303815481101561000057906000526020600020906005020160005b50815481556001808301549082015560028083018054918301805460f860020a60ff90941684029390930460ff19909316929092178083559054606060020a600160a060020a0361010092839004168102040261010060a860020a03199091161790556003808301549082015560049182015491015560008054839190839081101561000057906000526020600020906005020160005b50815181556020820151600182015560408201516002820180546060850151606060020a908102046101000261010060a860020a031960f860020a9485029490940460ff1990921691909117929092169190911790556080820151600382015560a0909101516004909101555b5b60001901610f56565b61139f565b50600154915060001982015b600081111561139f57600160018203815481101561000057906000526020600020906005020160005b5060010154600182815481101561000057906000526020600020906005020160005b5060010154111561139557600160018203815481101561000057906000526020600020906005020160005b506040805160c081018252825481526001808401546020830152600284015460ff8116151593830193909352610100909204600160a060020a031660608201526003830154608082015260049092015460a0830152805491935090829081101561000057906000526020600020906005020160005b50600160018303815481101561000057906000526020600020906005020160005b50815481556001808301548183015560028084018054918401805460f860020a60ff90941684029390930460ff19909316929092178083559054606060020a600160a060020a0361010092839004168102040261010060a860020a03199091161790556003808401549083015560049283015492909101919091558054839190839081101561000057906000526020600020906005020160005b50815181556020820151600182015560408201516002820180546060850151606060020a908102046101000261010060a860020a031960f860020a9485029490940460ff1990921691909117929092169190911790556080820151600382015560a0909101516004909101555b5b60001901611182565b5b5b50505050565b825182900380151561155157836040015115611487576000805460001981019081101561000057906000526020600020906005020160005b50600080825560018201819055600282018054600160a860020a03191690556003820181905560049091018190558054600019810180835591908290801582901161147d5760050281600502836000526020600020918201910161147d91905b8082111561077157600080825560018201819055600282018054600160a860020a0319169055600382018190556004820155600501610737565b5090565b5b5050505061154c565b6001805460001981019081101561000057906000526020600020906005020160005b5060008082556001808301829055600283018054600160a860020a031916905560038301829055600490920155805460001981018083559190829080158290116115465760050281600502836000526020600020918201910161154691905b8082111561077157600080825560018201819055600282018054600160a860020a0319169055600382018190556004820155600501610737565b5090565b5b505050505b6115af565b836040015115611587576000805482919060001981019081101561000057906000526020600020906005020160005b50556115af565b6001805482919060001981019081101561000057906000526020600020906005020160005b50555b5b828452602084018290526002805460018101808355828183801582901161162a5760050281600502836000526020600020918201910161162a91905b8082111561077157600080825560018201819055600282018054600160a860020a0319169055600382018190556004820155600501610737565b5090565b5b505050916000526020600020906005020160005b50855181556020860151600182015560408601516002820180546060890151606060020a908102046101000261010060a860020a031960f860020a9485029490940460ff1990921691909117929092169190911790556080860151600382015560a0860151600490910155505b5050505056";

    private OrderBook(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private OrderBook(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public Future<Bool> matchExists() {
        Function function = new Function("matchExists", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Uint256> getNumberOfSellOrders() {
        Function function = new Function("getNumberOfSellOrders", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<List<Type>> buyOrders(Uint256 param0) {
        Function function = new Function("buyOrders", 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return executeCallMultipleValueReturnAsync(function);
    }

    public Future<Uint256> getNumberOfExecutedOrders() {
        Function function = new Function("getNumberOfExecutedOrders", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> kill() {
        Function function = new Function("kill", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<List<Type>> sellOrders(Uint256 param0) {
        Function function = new Function("sellOrders", 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return executeCallMultipleValueReturnAsync(function);
    }

    public Future<Uint256> getNumberOfBuyOrders() {
        Function function = new Function("getNumberOfBuyOrders", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<List<Type>> executedOrders(Uint256 param0) {
        Function function = new Function("executedOrders", 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return executeCallMultipleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> removeParticipant(Address _participant) {
        Function function = new Function("removeParticipant", Arrays.<Type>asList(_participant), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Int256> topSellOrderId() {
        Function function = new Function("topSellOrderId", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> createOrder(Uint256 _quantity, Uint256 _price, Bool _buy, Uint256 _externId) {
        Function function = new Function("createOrder", Arrays.<Type>asList(_quantity, _price, _buy, _externId), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Bool> isParticipant(Address _participant) {
        Function function = new Function("isParticipant", 
                Arrays.<Type>asList(_participant), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Utf8String> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> cancelOrder(Int256 _orderId, Bool _buy) {
        Function function = new Function("cancelOrder", Arrays.<Type>asList(_orderId, _buy), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Int256> topBuyOrderId() {
        Function function = new Function("topBuyOrderId", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Bool> isPending(Uint256 _orderId) {
        Function function = new Function("isPending", 
                Arrays.<Type>asList(_orderId), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> executeMatch(Int256 _buyOrderId, Int256 _sellOrderId) {
        Function function = new Function("executeMatch", Arrays.<Type>asList(_buyOrderId, _sellOrderId), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> addParticipant(Address _participant) {
        Function function = new Function("addParticipant", Arrays.<Type>asList(_participant), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public static Future<OrderBook> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialValue, Utf8String _symbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_symbol));
        return deployAsync(OrderBook.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialValue);
    }

    public static Future<OrderBook> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialValue, Utf8String _symbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_symbol));
        return deployAsync(OrderBook.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialValue);
    }

    public static OrderBook load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new OrderBook(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static OrderBook load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new OrderBook(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
