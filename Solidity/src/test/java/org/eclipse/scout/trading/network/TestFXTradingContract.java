package org.eclipse.scout.trading.network;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

public class TestFXTradingContract {
	
	private static final Utf8String FIRMA1 = new Utf8String("Firma1");
	private static final Bool BUY = new Bool(true);	
	private static final Bool SELL = new Bool(false);
	// TODO use localhost if a local geth client is running or replace with the docker container IP: docker inspect <containerId> | grep IPAddress
	public static final String CLIENT_IP = "172.17.0.2";
	public static final String CLIENT_PORT = "8545";
	public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
	public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(500_000L);
	private static final String HEX_PREFIX = "0x";

	private static Web3j web3 = null;

	
	@BeforeClass
	public static void setUp() {
		try {
			String clientUrl = String.format("http://%s:%s", CLIENT_IP, CLIENT_PORT);
			web3 = Web3j.build(new HttpService(clientUrl));
		} catch (Exception e) {
			throw new RuntimeException("could not set up web3j", e);
		}
	}
	
	/** 
	 * Test class to check the scenario described here
	 * http://stackoverflow.com/questions/13112062/which-are-the-order-matching-algorithms-most-commonly-used-by-electronic-financi

	Id   Side    Time   Qty   Price   Qty    Time   Side  
	---+------+-------+-----+-------+-----+-------+------
	#3                        20.30   200   09:05   SELL  
	#1                        20.30   100   09:01   SELL  
	#2                        20.25   100   09:03   SELL  
	#5   BUY    09:08   200   20.20                       
	#4   BUY    09:06   100   20.15                       
	#6   BUY    09:09   200   20.15     */
	@Test
	public void deployFXTrading() {
		try {
			Web3ClientVersion versionResponse = web3.web3ClientVersion().sendAsync().get();
			
			String contractOwnerAdress = Alice.ADDRESS;
			String coinbase = getAccount(0);
			
//			getBalance(contractOwnerAdress);
//			getBalance(coinbase);
//
//			transferEther(coinbase, contractOwnerAdress, Convert.toWei("90", Convert.Unit.ETHER).toBigInteger());
//
//			getBalance(contractOwnerAdress);
//			getBalance(coinbase);
			
			System.out.println(versionResponse.getResult());
			Future<FXTrading> deploy = FXTrading.deploy(web3, Alice.CREDENTIALS, GAS_PRICE_DEFAULT, BigInteger.valueOf(2_000_000L), BigInteger.valueOf(0), new Utf8String("CHFEUR"));
			FXTrading contract = deploy.get();
			System.out.println(contract.getContractAddress());
			Utf8String x = contract.currencyPair().get();
			System.out.println(x.getValue());
			
			contract.createDeal(new Uint256(BigInteger.valueOf(100)), new Uint256(BigInteger.valueOf(2030)), SELL, FIRMA1).get();
			contract.createDeal(new Uint256(BigInteger.valueOf(100)), new Uint256(BigInteger.valueOf(2025)), SELL, FIRMA1).get();
			contract.createDeal(new Uint256(BigInteger.valueOf(200)), new Uint256(BigInteger.valueOf(2030)), SELL, FIRMA1).get();
			
			Assert.assertEquals(BigInteger.valueOf(2030), readDeal(false, 0, contract).price);
			Assert.assertEquals(BigInteger.valueOf(2030), readDeal(false, 1, contract).price);
			Assert.assertEquals(BigInteger.valueOf(2025), readDeal(false, 2, contract).price);
			
			contract.createDeal(new Uint256(BigInteger.valueOf(100)), new Uint256(BigInteger.valueOf(2015)), BUY, FIRMA1).get();
			contract.createDeal(new Uint256(BigInteger.valueOf(200)), new Uint256(BigInteger.valueOf(2020)), BUY, FIRMA1).get();
			contract.createDeal(new Uint256(BigInteger.valueOf(200)), new Uint256(BigInteger.valueOf(2015)), BUY, FIRMA1).get();
			
			Assert.assertEquals(BigInteger.valueOf(2015), readDeal(true, 0, contract).price);
			Assert.assertEquals(BigInteger.valueOf(2015), readDeal(true, 1, contract).price);
			Assert.assertEquals(BigInteger.valueOf(2020), readDeal(true, 2, contract).price);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private P_Deal readDeal(boolean buy, int index, FXTrading contract) throws InterruptedException, ExecutionException {
		List<Type> list;
		if (buy) {
			list = contract.buyDeals(new Uint256(BigInteger.valueOf(index))).get();
		} else {
			list = contract.sellDeals(new Uint256(BigInteger.valueOf(index))).get();
		}
		return new P_Deal(list);
				//buy ? new P_Deal(contract.buyDeals(new Uint256(BigInteger.valueOf(index))).get()) : new P_Deal(contract.sellDeals(new Uint256(BigInteger.valueOf(index))).get());
	}
	
	class P_Deal {
		public BigInteger quantity;
		public BigInteger price;
		public Boolean buy;
		public String company;
		public BigInteger dealNr;

		P_Deal(List<Type> list) {
			quantity = ((Uint256) list.get(0)).getValue();
			price = ((Uint256) list.get(1)).getValue();
			buy = ((Bool) list.get(2)).getValue();
			// TODO check whats wrong here
//			company = ((UTF8String) list.get(3)).get();// toString();
			dealNr = ((Uint256) list.get(4)).getValue();
		}
	}

	private String transferEther(String from, String to, BigInteger amount) throws Exception {
		BigInteger nonce = getNonce(from);

		Transaction transaction = new Transaction(from, nonce, GAS_PRICE_DEFAULT, GAS_LIMIT_DEFAULT, to, amount, null);
		EthSendTransaction txRequest = web3.ethSendTransaction(transaction).sendAsync().get();
		String txHash = txRequest.getTransactionHash();

		Assert.assertTrue(String.format("tx has error state %s", txRequest.getError()), !txRequest.hasError());
		Assert.assertTrue("tx hash is empty or null", txHash != null && txHash.startsWith(HEX_PREFIX));

		System.out.println("tx hash: " + txHash);
		System.out.println(String.format("amount: %d from: %s to %s", amount, from, to));

		return txHash;
	}

	private BigInteger getNonce(String address) throws Exception {
		EthGetTransactionCount txCount = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
				.sendAsync().get();
		BigInteger nonce = txCount.getTransactionCount();

		Assert.assertTrue(String.format("nonce null for account %s", address), nonce != null);
		Assert.assertTrue(String.format("nonce is negative %d account %s", nonce, address),
				nonce.compareTo(new BigInteger("0")) >= 0);

		return nonce;
	}

	private String getAccount(int i) throws Exception {
		EthAccounts accountsResponse = web3.ethAccounts().sendAsync().get();
		List<String> accounts = accountsResponse.getAccounts();

		return accounts.get(i);
	}
	
	private BigInteger getBalance(String address) throws Exception {
		return hasWeis(address, new BigInteger("0"));
	}
	
	private BigInteger hasWeis(String address, BigInteger minWeiAmount) throws Exception{
		return hasWeis(address, minWeiAmount, true);
	}

	private BigInteger hasWeis(String address, BigInteger minWeiAmount, boolean sysout) throws Exception{
		EthGetBalance balanceResponse = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
		BigInteger balance = balanceResponse.getBalance();

		if(sysout) {
			System.out.println(String.format("balance: %d account: %s" , balance, address));
		}

		Assert.assertTrue(String.format("not enough weis, expected at least %d, available %d for address %s", minWeiAmount, balance, address), balance.compareTo(minWeiAmount) >= 0);

		return balance;
	}

}

