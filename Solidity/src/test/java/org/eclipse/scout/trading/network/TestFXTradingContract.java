package org.eclipse.scout.trading.network;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

public class TestFXTradingContract {
	
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
	
	@Test
	public void deployFXTrading() {
		try {
			Web3ClientVersion versionResponse = web3.web3ClientVersion().sendAsync().get();
			
			String contractOwnerAdress = Alice.ADDRESS;
			String coinbase = getAccount(0);
			
			getBalance(contractOwnerAdress);
			getBalance(coinbase);

			transferEther(coinbase, contractOwnerAdress, Convert.toWei("90", Convert.Unit.ETHER).toBigInteger());

			getBalance(contractOwnerAdress);
			getBalance(coinbase);
			
			System.out.println(versionResponse.getResult());
			Future<FXTrading> deploy = FXTrading.deploy(web3, Alice.CREDENTIALS, GAS_PRICE_DEFAULT, BigInteger.valueOf(2_000_000L), BigInteger.valueOf(0), new Utf8String("CHFEUR"));
			FXTrading contract = deploy.get();
			System.out.println(contract.getContractAddress());
			Utf8String x = contract.currencyPair().get();
			System.out.println(x.getValue());
			
//			contract.createDeal(_quantity, _price, _buy, _company);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
