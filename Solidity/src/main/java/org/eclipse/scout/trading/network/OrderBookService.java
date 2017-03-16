package org.eclipse.scout.trading.network;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.scout.trading.network.tool.Web3jHelper;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;

/**
 * Wrapper class for generated contract class. Keeps app code separate from
 * smart contract code.
 */

public class OrderBookService {

	private OrderBook contract;

	/**
	 * Deploys the order book service contract. The method blocks until the
	 * contract is deployed.
	 *
	 * @return address of the deployed contract
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public String deploy(Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String symbol)
			throws InterruptedException, ExecutionException {
		contract = OrderBook
				.deploy(Web3jHelper.getWeb3j(), credentials, gasPrice, gasLimit, BigInteger.valueOf(0), new Utf8String(symbol))
				.get();

		System.out.println("contract successfully deployed at address" + contract.getContractAddress());

		return contract.getContractAddress();
	}

	/**
	 * Submit the specified order.
	 *
	 * @param type
	 *            order type
	 * @param amount
	 * @param price
	 * @return the id of the new order
	 */
	public int publish(Order order) {
		return -1;
	}

	/**
	 * Gets the specified order.
	 *
	 * @param int
	 *            order id
	 * @return the order of it exists in the order book. null otherwise.
	 */
	public Order get(int order) {
		return null;
	}

	/**
	 * Provides the sorted list of orders in the order book.
	 *
	 * @return
	 */
	public List<Order> get() {
		return null;
	}

	/**
	 * Removes the order from the order book
	 *
	 * @param order
	 *            the id of the order to cancel
	 * @return true if the order has been successfully deleted
	 */
	public boolean remove(int order) {
		return false;
	}
}
