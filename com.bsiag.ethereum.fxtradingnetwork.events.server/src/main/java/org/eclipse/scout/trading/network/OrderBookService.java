package org.eclipse.scout.trading.network;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import com.bsiag.ethereum.fxtradingnetwork.events.account.EthereumService;
import com.bsiag.ethereum.fxtradingnetwork.events.account.FXTrading;

/**
 * Wrapper class for generated contract class. Keeps app code separate from
 * smart contract code.
 */

@ApplicationScoped
public class OrderBookService {

	private static final Logger LOG = LoggerFactory.getLogger(EthereumService.class);

	private FXTrading contract;

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
		contract = FXTrading
				.deploy(getWeb3j(), credentials, gasPrice, gasLimit, BigInteger.valueOf(0), new Utf8String(symbol))
				.get();

		LOG.info("contract successfully deployed at address" + contract.getContractAddress());

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

	private Web3j getWeb3j() {
		return BEANS.get(EthereumService.class).getWeb3j();
	}
}
