package com.bsiag.ethereum.fxtradingnetwork.events.account;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response.Error;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthCoinbase;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.MessageDecodingException;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import com.bsiag.ethereum.fxtradingnetwork.events.account.model.Account;
import com.bsiag.ethereum.fxtradingnetwork.events.account.model.Transaction;
import com.bsiag.ethereum.fxtradingnetwork.events.server.OrderBook;

@ApplicationScoped
public class EthereumService {

  private static final Logger LOG = LoggerFactory.getLogger(EthereumService.class);

  // switch for testrpc/infura
  private static final boolean USE_TESTRPC = true;

  // testrpc coordinates
  private static final String CLIENT_IP = "192.168.99.100";
  private static final String CLIENT_PORT = "8545";

  // infura coordinates
  private static final String TOKEN = "3UMFlH4jlpWx6IqttMeG";
  private static final String ETHEREUM_MAIN = "https://mainnet.infura.io/" + TOKEN;
  // private static final String ETHEREUM_TEST = "https://ropsten.infura.io/" + TOKEN;

  // copied from org.web3j.protocol.core.methods.response.Numeric
  private static final String HEX_PREFIX = "0x";

  public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
  public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(40_000L);

  // the connection to the ethereum net
  private Web3j web3j = null;

  private OrderBook contract = null;
  private String rpc_coinbase;

  // TODO replace these with some real persistence
  private static Map<String, Account> wallets = new HashMap<>();
  // transactions need to be persisted as well as ethereum currently does not offer an api to list all tx for an account
  // also see https://github.com/ethereum/go-ethereum/issues/1897
  private static Map<UUID, Transaction> transactions = new HashMap<>();

  @PostConstruct
  private void init() {
//		LOG.info("Poulating dummy/temp accounts ...");
//		populateAccount("prs01", "UTC--2016-12-12T08-09-51.487000000Z--8d2ec831056c620fea2fabad8bf6548fc5810cc3.json", "123");
//		populateAccount("prs01a", "UTC--2016-12-12T09-07-24.203000000Z--cbc12f306da804bb681aceeb34f0bc58ba2f7ad7.json", "456");
//		LOG.info("local wallets successfully loaded ...");

    // move some initial funds to alice's account (only when working with testrpc)
    if (USE_TESTRPC) {
      try {
        EthCoinbase coinbase = getWeb3j().ethCoinbase().sendAsync().get();
        String from = coinbase.getAddress();
        BigDecimal balance = getBalance(from);
        LOG.info("coinbase: " + from + ", balance: " + balance.toString());
        rpc_coinbase = from;
//        String to = "0x8d2ec831056c620fea2fabad8bf6548fc5810cc3";
//        BigInteger amount = Convert.toWei("10", Unit.ETHER).toBigInteger();
//
//        BigInteger nonce = getNonce(from);
//
//        org.web3j.protocol.core.methods.request.Transaction transaction =
//            new org.web3j.protocol.core.methods.request.Transaction(from, nonce, Transaction.GAS_PRICE_DEFAULT, Transaction.GAS_LIMIT_DEFAULT, to, amount, null);
//
//        EthSendTransaction txRequest = getWeb3j().ethSendTransaction(transaction).sendAsync().get();
//        LOG.info(String.format("added %d weis to account of prs01. tx hash: %s", amount, txRequest.getTransactionHash()));

        String contractOwnerAdress = Alice.ADDRESS;
        if (getBalance(contractOwnerAdress).compareTo(Convert.toWei("10", Convert.Unit.ETHER)) < 0) {
          transferEther(rpc_coinbase, contractOwnerAdress, Convert.toWei("10", Convert.Unit.ETHER).toBigInteger());
        }

        // TODO check if this is a good place
//        contract = deployContract();
      }
      catch (Exception e) {
        LOG.error(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private OrderBook deployContract() throws Exception {
    String contractOwnerAdress = Alice.ADDRESS;

    if (getBalance(contractOwnerAdress).compareTo(Convert.toWei("10", Convert.Unit.ETHER)) < 0) {
      transferEther(rpc_coinbase, contractOwnerAdress, Convert.toWei("10", Convert.Unit.ETHER).toBigInteger());
    }

    LOG.info(getBalance(contractOwnerAdress).toString());

    Future<OrderBook> deploy = OrderBook.deploy(getWeb3j(), Alice.CREDENTIALS, GAS_PRICE_DEFAULT, BigInteger.valueOf(2_000_000L), BigInteger.valueOf(0), new Utf8String("CHFEUR"));
    OrderBook contract = deploy.get();
    System.out.println(contract.getContractAddress());

    return contract;
  }

  public BigInteger createDeal(String company, boolean type, int quantity, double price) throws Exception {
    // 			contract.createDeal(new Uint256(BigInteger.valueOf(100)), new Uint256(BigInteger.valueOf(2030)), SELL, FIRMA1).get();
    BigInteger dealNr = null;

    Utf8String owner = new Utf8String(company);
    Bool buy = new Bool(type);
    Uint256 dealQuantity = new Uint256(BigInteger.valueOf(quantity));
    Uint256 dealPrice = new Uint256(BigInteger.valueOf((long) (100 * price)));

    TransactionReceipt receipt = contract.createOrder(dealQuantity, dealPrice, buy).get();
    LOG.info(receipt.getTransactionHash());

    for (int i = 0; i < 10; i++) {
      P_Deal deal = readDeal(type, i, contract);
      if (null != deal) {
        LOG.info("deal nr: " + deal.dealNr + " price: " + deal.price + " quatity: " + deal.quantity);
      }
      else {
        break;
      }
    }

    return dealNr;
  }

  private P_Deal readDeal(boolean buy, int index, OrderBook contract) throws InterruptedException, ExecutionException {
    List<Type> list;
    if (buy) {
      list = contract.buyOrders(new Uint256(BigInteger.valueOf(index))).get();
    }
    else {
      list = contract.sellOrders(new Uint256(BigInteger.valueOf(index))).get();
    }
    P_Deal deal = null;
    if (null != list && list.size() > 0) {
      deal = new P_Deal(list);
    }
    return deal;
    //buy ? new P_Deal(contract.buyDeals(new Uint256(BigInteger.valueOf(index))).get()) : new P_Deal(contract.sellDeals(new Uint256(BigInteger.valueOf(index))).get());
  }

  class P_Deal {
    public BigInteger quantity;
    public BigInteger price;
    public Boolean buy;
    public String company;
    public BigInteger dealNr;

    P_Deal(List<Type> list) {
      if (null != list && list.size() > 0) {
        quantity = ((Uint256) list.get(0)).getValue();
        price = ((Uint256) list.get(1)).getValue();
        buy = ((Bool) list.get(2)).getValue();
        // TODO check whats wrong here
//			company = ((UTF8String) list.get(3)).get();// toString();
        dealNr = ((Uint256) list.get(4)).getValue();
      }
    }
  }

  private String transferEther(String from, String to, BigInteger amount) throws Exception {
    BigInteger nonce = getNonce(from);

    org.web3j.protocol.core.methods.request.Transaction transaction = new org.web3j.protocol.core.methods.request.Transaction(from, nonce, GAS_PRICE_DEFAULT, GAS_LIMIT_DEFAULT, to, amount, null);
    EthSendTransaction txRequest = getWeb3j().ethSendTransaction(transaction).sendAsync().get();
    String txHash = txRequest.getTransactionHash();

    System.out.println("tx hash: " + txHash);
    System.out.println(String.format("amount: %d from: %s to %s", amount, from, to));

    return txHash;
  }

  private String getAccount(int i) throws Exception {
    EthAccounts accountsResponse = getWeb3j().ethAccounts().sendAsync().get();
    List<String> accounts = accountsResponse.getAccounts();

    return accounts.get(i);
  }

  private void populateAccount(String personId, String fileName, String password) {
    String walletName = "Primary Account";
    String walletPath = "C:\\Users\\mzi\\AppData\\Local\\Temp";
    Account wallet = Account.load(walletName, password, walletPath, fileName);
    wallet.setPersonId(personId);

    save(wallet);
  }

  public String createTransaction(String from, String to, BigInteger amountWei, String data, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit) {

    if (from == null || to == null || amountWei == null) {
      return null;
    }

    Account wallet = getWallet(from);
    if (wallet == null) {
      return null;
    }

    if (nonce == null) {
      nonce = getNonce(from);
    }

    if (gasPrice == null) {
      gasPrice = Transaction.GAS_PRICE_DEFAULT;
    }

    if (gasLimit == null) {
      gasLimit = Transaction.GAS_LIMIT_DEFAULT;
    }

    Transaction tx = wallet.createSignedTransaction(to, amountWei, data, nonce, gasPrice, gasLimit);
    save(tx);

    return tx.getId().toString();
  }

  public Set<String> getWallets() {
    return wallets.keySet();
  }

  public Set<String> getWallets(String personId) {
    if (personId == null) {
      return getWallets();
    }

    return wallets.values()
        .stream()
        .filter(wallet -> personId.equals(wallet.getPersonId()))
        .map(wallet -> wallet.getAddress())
        .collect(Collectors.toSet());
  }

  public Account getWallet(String address) {
    return wallets.get(address);
  }

  public void save(Account wallet) {
    LOG.info("caching wallet '" + wallet.getFileName() + "' with address '" + wallet.getAddress() + "'");

    wallets.put(wallet.getAddress(), wallet);
  }

  public Set<String> getTransactions() {
    return transactions.keySet()
        .stream()
        .map(id -> id.toString())
        .collect(Collectors.toSet());
  }

  public Transaction getTransaction(String id) {
    return transactions.get(UUID.fromString(id));
  }

  public void save(Transaction transaction) {
    LOG.info("Caching tx from: " + transaction.getFromAddress() + " to: " + transaction.getToAddress() + " with amount " + transaction.getValue() + " and hash: " + transaction.getHash());

    transactions.put(transaction.getId(), transaction);
  }

  public Web3j getWeb3j() {
    if (web3j == null) {
      LOG.info("Trying to connect to Ethereum net ...");

      if (USE_TESTRPC) {
        String clientUrl = String.format("http://%s:%s", CLIENT_IP, CLIENT_PORT);
        web3j = Web3j.build(new HttpService(clientUrl));
      }
      else {
        web3j = Web3j.build(new InfuraHttpService(ETHEREUM_MAIN));
      }
      LOG.info("Successfully connected");
    }

    return web3j;
  }

  public BigDecimal getBalance(String address) {
    return getBalance(address, Unit.ETHER);
  }

  public BigDecimal getBalance(String address, Unit unit) {
    if (address == null || unit == null) {
      return null;
    }

    BigInteger balance = getBalanceWei(address);

    if (balance == null) {
      return null;
    }

    return Convert.fromWei(new BigDecimal(balance), unit);
  }

  public BigInteger getBalanceWei(String address) {
    try {
      EthGetBalance balanceResponse = getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
      BigInteger balance = getBalanceFix(balanceResponse);
      return balance;

      // return balanceResponse.getBalance();
    }
    catch (Exception e) {
      throw new ProcessingException("Failed to get balance for address '" + address + "'", e);
    }
  }

  public Transaction send(Transaction tx) {
    LOG.info("Sending TX ...");
    EthSendTransaction ethSendTransaction = null;

    try {
      ethSendTransaction = getWeb3j().ethSendRawTransaction(tx.getSignedContent()).sendAsync().get();
    }
    catch (Exception e) {
      throw new ProcessingException("Failed to send transaction " + tx.getSignedContent(), e);
    }

    checkResponseFromSending(ethSendTransaction);

    tx.setSent(new Date());
    tx.setError(ethSendTransaction.getError());
    tx.setHash(ethSendTransaction.getTransactionHash());
    tx.setStatus(Transaction.PENDING);
    LOG.info("Successfully sent TX. Hash: " + tx.getHash());

    save(tx);

    return tx;
  }

  private void checkResponseFromSending(EthSendTransaction response) {
    Error error = response.getError();
    String result = response.getResult();

    if (error != null) {
      String message = "Failed to send transaction: " + error.getMessage();
      LOG.error(message);
      throw new ProcessingException(message);
    }
    else {
      LOG.info("result:" + result);
    }
  }

  public Transaction refreshStatus(String transactionId) {
    LOG.info("Polling TX status...");

    Transaction tx = getTransaction(transactionId);
    EthGetTransactionReceipt txReceipt = null;

    try {
      txReceipt = getWeb3j().ethGetTransactionReceipt(tx.getHash()).sendAsync().get();
    }
    catch (Exception e) {
      throw new ProcessingException("failed to poll status for transaction " + tx.getSignedContent(), e);
    }

    tx.setTransactionReceipt(txReceipt.getResult());
    LOG.info("Successfully polled status. Status: " + tx.getStatus());

    save(tx);

    return tx;
  }

  public BigInteger getNonce(String address) {
    LOG.info("Getting nonce for address " + address + " ...");
    // TODO cleanup once web3j takes care of testrpc encoding bug
    //    EthGetTransactionCount txCount;
    //
    //    try {
    //      txCount = getWeb3j().ethGetTransactionCount(
    //          address, DefaultBlockParameterName.LATEST).sendAsync().get();
    //    }
    //    catch (Exception e) {
    //      throw new ProcessingException("failed to get nonce for address '" + address + "'");
    //    }
    //
    //    BigInteger nonce = txCount.getTransactionCount();

    BigInteger nonce = null;

    try {
      nonce = getNonceFix(address);
      LOG.info("Successfully got nonce: " + nonce);
    }
    catch (Exception e) {
      LOG.error("error getting nonce for address " + address, e);
    }

    return nonce;
  }

  private BigInteger getNonceFix(String address) throws Exception {
    LOG.info("Getting nonce for address " + address + " ...");

    EthGetTransactionCount txCount = getWeb3j().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
    // TODO fix this once web3j accouts for 'bug' in testrpc
    BigInteger nonce = getTransactionCount(txCount);

    LOG.info("Successfully got nonce: " + nonce);

    return nonce;
  }

  // copied from org.web3j.protocol.core.methods.response.EthGetTransactionCount
  private BigInteger getTransactionCount(EthGetTransactionCount txCount) {
    String count = txCount.getResult();
    return decodeQuantity(count);
  }

  // copied from org.web3j.protocol.core.methods.response.EthGetBalance
  private BigInteger getBalanceFix(EthGetBalance balanceResponse) {
    String balance = balanceResponse.getResult();
    return decodeQuantity(balance);
  }

  // copied from org.web3j.protocol.core.methods.response.Numeric
  private static BigInteger decodeQuantity(String value) {
    if (!isValidHexQuantity(value)) {
      throw new MessageDecodingException("Value must be in format 0x[1-9]+[0-9]* or 0x0");
    }
    try {
      return new BigInteger(value.substring(2), 16);
    }
    catch (NumberFormatException e) {
      throw new MessageDecodingException("Negative ", e);
    }
  }

  // copied from org.web3j.protocol.core.methods.response.Numeric
  private static boolean isValidHexQuantity(String value) {
    if (value == null) {
      return false;
    }

    if (value.length() < 3) {
      return false;
    }

    if (!value.startsWith(HEX_PREFIX)) {
      return false;
    }

    // If TestRpc resolves the following issue, we can reinstate this code
    // https://github.com/ethereumjs/testrpc/issues/220
    //        if (value.length() > 3 && value.charAt(2) == '0') {
    //            return false;
    //        }

    return true;
  }

}
