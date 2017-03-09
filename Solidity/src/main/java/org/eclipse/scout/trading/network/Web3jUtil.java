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
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

/**
 * Helper class containing various methods to interact with the blockchain
 */
public final class Web3jUtil {
	
	private Web3jUtil() {}
	
	public static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
	public static final BigInteger GAS_LIMIT_DEFAULT = BigInteger.valueOf(500_000L);
	
	public static String getAccount(Web3j web3j, int index) throws Exception {
		EthAccounts accountsResponse = web3j.ethAccounts().sendAsync().get();
		return accountsResponse.getAccounts().get(index);
	}
	
	public static BigDecimal getBalance(Web3j web3j, String address, Unit unit) throws Exception {
		EthGetBalance balanceResponse = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
		BigInteger balance = balanceResponse.getBalance();
		return Convert.fromWei(balance.toString(), unit);
	}

	public static String transfer(Web3j web3j, String from, String to, int amount, Unit unit) throws InterruptedException, ExecutionException {
		BigInteger nonce = getNonce(web3j, from);
		BigInteger weiAmount = Convert.toWei(BigDecimal.valueOf(amount), unit).toBigInteger();
		Transaction transaction = new Transaction(from, nonce, GAS_PRICE_DEFAULT, GAS_LIMIT_DEFAULT, to, weiAmount, null);
		EthSendTransaction txRequest = web3j.ethSendTransaction(transaction).sendAsync().get();
		return txRequest.getTransactionHash();
	}
	

	private static BigInteger getNonce(Web3j web3j, String address) throws InterruptedException, ExecutionException {
		EthGetTransactionCount txCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
				.sendAsync().get();
		BigInteger nonce = txCount.getTransactionCount();
		return nonce;
	}

}
