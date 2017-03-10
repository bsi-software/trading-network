package org.eclipse.scout.trading.network;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

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
import org.web3j.utils.Convert.Unit;

/**
 * Helper class containing various methods to interact with the blockchain
 */
public final class Web3jUtil {
	
	private Web3jUtil() {}
	
	public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
	public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(500_000L);
	
	private static final String CLIENT_IP = "192.168.99.100";
	private static final String CLIENT_PORT = "8545";
	private static Web3j web3j = null;
	
	public static Web3j getWeb3j() {
		return web3j == null ? connect() : web3j;
	}
	
	public static Web3j connect() {
		System.out.println("Trying to connect to Ethereum net ...");
		String clientUrl = String.format("http://%s:%s", CLIENT_IP, CLIENT_PORT);
		
		web3j = Web3j.build(new HttpService(clientUrl));
		
		System.out.println("Connected to " + getClientVersion());
		return web3j;
	}
	
	public static String getClientVersion() {
		try {
			Web3ClientVersion versionResponse;
			versionResponse = web3j.web3ClientVersion().sendAsync().get();
			return versionResponse.getWeb3ClientVersion();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return "<undefined>";
		}
	}
	
	public static String getAccount(int index) throws Exception {
		EthAccounts accountsResponse = getWeb3j().ethAccounts().sendAsync().get();
		return accountsResponse.getAccounts().get(index);
	}
	
	public static BigDecimal getBalance(String address, Unit unit) throws Exception {
		EthGetBalance balanceResponse = getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
		BigInteger balance = balanceResponse.getBalance();
		return Convert.fromWei(balance.toString(), unit);
	}

	public static String transfer(String from, String to, int amount, Unit unit) throws InterruptedException, ExecutionException {
		BigInteger nonce = getNonce(from);
		BigInteger weiAmount = Convert.toWei(BigDecimal.valueOf(amount), unit).toBigInteger();
		Transaction transaction = new Transaction(from, nonce, GAS_PRICE_DEFAULT, GAS_LIMIT_DEFAULT, to, weiAmount, null);
		EthSendTransaction txRequest = getWeb3j().ethSendTransaction(transaction).sendAsync().get();
		return txRequest.getTransactionHash();
	}
	

	private static BigInteger getNonce(String address) throws InterruptedException, ExecutionException {
		EthGetTransactionCount txCount = getWeb3j().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
				.sendAsync().get();
		BigInteger nonce = txCount.getTransactionCount();
		return nonce;
	}

}
