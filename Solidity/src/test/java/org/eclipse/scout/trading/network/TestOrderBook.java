package org.eclipse.scout.trading.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.scout.trading.network.tool.Web3jHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Convert.Unit;

/**
 * unit test for the {@link FXTrading} smart contract.
 * 
 * create sol, bin + abi files 
 * https://ethereum.github.io/browser-solidity/#version=soljson-v0.4.9+commit.364da425.js
 * 
 * create java class files
 * mzi@BSIM3236 /c/eclipse/neon/web3j_repo/web3j/build/install/web3j/bin (master)
 * ./web3j solidity generate FXTrading.bin FXTrading.abi -o . -p org.eclipse.scout.trading.network
 */
public class TestOrderBook {

	private static final Bool BUY = new Bool(true);	
	private static final Bool SELL = new Bool(false);

	private static enum Queue {Buy, Sell, Executed};

	private static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
	private static final BigInteger GAS_LIMIT_CONTRACT_DEPLOY = BigInteger.valueOf(4_700_000L);

	private static final Uint256 QUANTITY_100 = new Uint256(BigInteger.valueOf(100));
	private static final Uint256 QUANTITY_200 = new Uint256(BigInteger.valueOf(200));
	private static final Uint256 QUANTITY_250 = new Uint256(BigInteger.valueOf(250));
	private static final Uint256 PRICE_2015 = new Uint256(BigInteger.valueOf(2015));
	private static final Uint256 PRICE_2020 = new Uint256(BigInteger.valueOf(2020));
	private static final Uint256 PRICE_2025 = new Uint256(BigInteger.valueOf(2025));
	private static final Uint256 PRICE_2030 = new Uint256(BigInteger.valueOf(2030));
	private static final Uint256 PRICE_2035 = new Uint256(BigInteger.valueOf(2035));
	private static final Uint256 EXTERN_ID_SELL1 = new Uint256(BigInteger.valueOf(123));
	private static final Uint256 EXTERN_ID_SELL2 = new Uint256(BigInteger.valueOf(456));
	private static final Uint256 EXTERN_ID_SELL3 = new Uint256(BigInteger.valueOf(789));
	private static final Uint256 EXTERN_ID_BUY1 = new Uint256(BigInteger.valueOf(147));
	private static final Uint256 EXTERN_ID_BUY2 = new Uint256(BigInteger.valueOf(258));
	private static final Uint256 EXTERN_ID_BUY3 = new Uint256(BigInteger.valueOf(369));
	private static final Uint256 EXTERN_ID_BUY4 = new Uint256(BigInteger.valueOf(159));

	private static final Int256 ORDER_ID_1 = new Int256(BigInteger.ONE);

	private static final Utf8String CONTRACT_SYMBOL = new Utf8String("CHFEUR");

	@BeforeClass
	public static void setUp() {
		try {
			String ownerAddress = Alice.ADDRESS;
			String coinbaseAddress = Web3jHelper.getAccount(0);
			BigDecimal coinbaseBalance = Web3jHelper.getBalance(coinbaseAddress, Unit.ETHER);
			BigDecimal ownerBalance = Web3jHelper.getBalance(ownerAddress, Unit.ETHER);
			System.out.println(String.format("Coinbase balance [Ether] %s with address %s", coinbaseBalance, coinbaseAddress));
			System.out.println(String.format("Contact owner balence [Ether] %s with address %s", ownerBalance, ownerAddress));

			if (ownerBalance.compareTo(BigDecimal.TEN) < 0) {
				String txHash = Web3jHelper.transfer(coinbaseAddress, ownerAddress, 10, Unit.ETHER);
				System.out.println(String.format("10 ether to owner sent, txHash=%s", txHash));
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not set up unit test", e);
		}
	}

	@Test
	public void testConnection() {
		Assert.assertNotNull("Web3j is null", Web3jHelper.getWeb3j());
		Assert.assertNotEquals("Unexpected ethereum client version: ", "<undefined>", Web3jHelper.getClientVersion());
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
	public void testOrderSorting() {
		OrderBook contract = null;
		try {
			contract = deployContract();

			initSellOrders(contract);
			assertEquals(PRICE_2030, readOrder(Queue.Sell, 0, contract).price);
			assertEquals(PRICE_2030, readOrder(Queue.Sell, 1, contract).price);
			assertEquals(PRICE_2025, readOrder(Queue.Sell, 2, contract).price);

			initBuyOrders(contract);
			assertEquals(PRICE_2015, readOrder(Queue.Buy, 0, contract).price);
			assertEquals(PRICE_2015, readOrder(Queue.Buy, 1, contract).price);
			assertEquals(PRICE_2020, readOrder(Queue.Buy, 2, contract).price);

		} catch (Exception e) {
			throw new RuntimeException("Exception during unit test", e);
		} finally {
			killContract(contract);
		}
	}

	private void initSellOrders(OrderBook contract) throws Exception {
		contract.createOrder(QUANTITY_100, PRICE_2030, SELL, EXTERN_ID_SELL1).get();
		contract.createOrder(QUANTITY_100, PRICE_2025, SELL, EXTERN_ID_SELL2).get();
		contract.createOrder(QUANTITY_200, PRICE_2030, SELL, EXTERN_ID_SELL3).get();

	}

	private void initBuyOrders(OrderBook contract) throws Exception {
		contract.createOrder(QUANTITY_100, PRICE_2015, BUY, EXTERN_ID_BUY1).get();
		contract.createOrder(QUANTITY_200, PRICE_2020, BUY, EXTERN_ID_BUY2).get();
		contract.createOrder(QUANTITY_200, PRICE_2015, BUY, EXTERN_ID_BUY3).get();
	}

	@Test
	public void testOrderMatcher() {
		OrderBook contract = null;
		try {
			contract = deployContract();

			initSellOrders(contract);
			initBuyOrders(contract);

			BigInteger buyOrders = contract.getNumberOfBuyOrders().get().getValue();
			BigInteger sellOrders = contract.getNumberOfSellOrders().get().getValue();
			BigInteger executedOrders = contract.getNumberOfExecutedOrders().get().getValue();

			assertEquals("Wrong number of buy orders", BigInteger.valueOf(3), buyOrders);
			assertEquals("Wrong number of sell orders", BigInteger.valueOf(3), sellOrders);
			assertEquals("Wrong number of executed orders", BigInteger.valueOf(0), executedOrders);
			assertFalse("No match expected", contract.matchExists().get().getValue());

			// try to force match
			Int256 buyOrderId = contract.topBuyOrderId().get();
			Int256 sellOrderId = contract.topSellOrderId().get();
			assertTrue("Matching buy order id <= 0", buyOrderId.getValue().longValue() > 0);
			assertTrue("Matching sell order id <= 0", sellOrderId.getValue().longValue() > 0);

			contract.executeMatch(buyOrderId, sellOrderId).get();

			// add matching order
			contract.createOrder(QUANTITY_250, PRICE_2035, BUY, EXTERN_ID_BUY4).get();
			buyOrders = contract.getNumberOfBuyOrders().get().getValue();
			assertEquals("Wrong number of buy orders", BigInteger.valueOf(4), buyOrders);

			// check if match is found
			assertTrue("1st Match expected", contract.matchExists().get().getValue());

			// get matching orders
			buyOrderId = contract.topBuyOrderId().get();
			sellOrderId = contract.topSellOrderId().get();
			assertEquals("Bad matching buy order id", BigInteger.valueOf(7), buyOrderId.getValue());
			assertEquals("Bad matching sell order id", BigInteger.valueOf(2), sellOrderId.getValue());

			// execute match
			System.out.println("Execute 1st match -> BUY:" + buyOrderId.getValue() + " SELL:" + sellOrderId.getValue());
			contract.executeMatch(buyOrderId, sellOrderId).get();
			printQueueStatus(contract);

			buyOrders = contract.getNumberOfBuyOrders().get().getValue();
			sellOrders = contract.getNumberOfSellOrders().get().getValue();
			executedOrders = contract.getNumberOfExecutedOrders().get().getValue();

			assertEquals("Wrong number of buy orders", BigInteger.valueOf(4), buyOrders);
			assertEquals("Wrong number of sell orders", BigInteger.valueOf(2), sellOrders);
			assertEquals("Wrong number of executed orders", BigInteger.valueOf(2), executedOrders);
			
			assertEquals("Bad execution buy order id at index 0", BigInteger.valueOf(7), readOrder(Queue.Executed, 0, contract).id.getValue());
			assertEquals("Bad execution buy order price at index 0", BigInteger.valueOf(2030), readOrder(Queue.Executed, 0, contract).price.getValue());
			assertEquals("Bad execution buy order quantity at index 0", BigInteger.valueOf(100), readOrder(Queue.Executed, 0, contract).quantity.getValue());
			
			assertEquals("Bad execution sell order id at index 1", BigInteger.valueOf(2), readOrder(Queue.Executed, 1, contract).id.getValue());
			assertEquals("Bad execution sell order price at index 1", BigInteger.valueOf(2030), readOrder(Queue.Executed, 1, contract).price.getValue());
			assertEquals("Bad execution sell order quantity at index 1", BigInteger.valueOf(100), readOrder(Queue.Executed, 1, contract).quantity.getValue());
			
			assertEquals("Bad buy order id at index 3", BigInteger.valueOf(7), readOrder(Queue.Buy, 3, contract).id.getValue());
			assertEquals("Bad buy order price at index 3", BigInteger.valueOf(2035), readOrder(Queue.Buy,3, contract).price.getValue());
			assertEquals("Bad buy order quantity at index 3", BigInteger.valueOf(150), readOrder(Queue.Buy, 3, contract).quantity.getValue());
			
			// check if match is found
			assertTrue("2nd Match expected", contract.matchExists().get().getValue());
			
			buyOrderId = contract.topBuyOrderId().get();
			sellOrderId = contract.topSellOrderId().get();
			assertEquals("Bad matching buy order id", BigInteger.valueOf(7), buyOrderId.getValue());
			assertEquals("Bad matching sell order id", BigInteger.valueOf(3), sellOrderId.getValue());

			// execute match
			System.out.println("Execute 2nd match -> BUY:" + buyOrderId.getValue() + " SELL:" + sellOrderId.getValue());
			contract.executeMatch(buyOrderId, sellOrderId).get();
			printQueueStatus(contract);

			buyOrders = contract.getNumberOfBuyOrders().get().getValue();
			sellOrders = contract.getNumberOfSellOrders().get().getValue();
			executedOrders = contract.getNumberOfExecutedOrders().get().getValue();

			assertEquals("Wrong number of buy orders", BigInteger.valueOf(3), buyOrders);
			assertEquals("Wrong number of sell orders", BigInteger.valueOf(2), sellOrders);
			assertEquals("Wrong number of executed orders", BigInteger.valueOf(4), executedOrders);
			
			assertEquals("Bad execution buy order id at index 2", BigInteger.valueOf(7), readOrder(Queue.Executed, 2, contract).id.getValue());
			assertEquals("Bad execution buy order price at index 2", BigInteger.valueOf(2032), readOrder(Queue.Executed, 2, contract).price.getValue());
			assertEquals("Bad execution buy order quantity at index 2", BigInteger.valueOf(150), readOrder(Queue.Executed, 2, contract).quantity.getValue());
			
			assertEquals("Bad execution sell order id at index 3", BigInteger.valueOf(3), readOrder(Queue.Executed, 3, contract).id.getValue());
			assertEquals("Bad execution sell order price at index 3", BigInteger.valueOf(2032), readOrder(Queue.Executed, 3, contract).price.getValue());
			assertEquals("Bad execution sell order quantity at index 3", BigInteger.valueOf(150), readOrder(Queue.Executed, 3, contract).quantity.getValue());

			// check if match is found
			assertFalse("No 3rd match expected", contract.matchExists().get().getValue());
			
		} catch (Exception e) {
			throw new RuntimeException("Exception during unit test", e);
		} finally {
			killContract(contract);
		}
	}

	@Test
	public void testDeployContract() {
		OrderBook contract = null;
		try {
			contract = deployContract();
			System.out.println("Contract symbol: " + contract.symbol().get().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception during unit test", e);
		} finally {
			killContract(contract);
		}
	}

	@Test
	public void testOrderRemoving() {
		OrderBook contract = null;
		try {
			contract = deployContract();

			contract.createOrder(QUANTITY_100, PRICE_2025, SELL, EXTERN_ID_SELL1).get();
			contract.createOrder(QUANTITY_100, PRICE_2030, SELL, EXTERN_ID_SELL2).get();
			contract.createOrder(QUANTITY_200, PRICE_2015, SELL, EXTERN_ID_SELL3).get();

			printQueueStatus(contract, Queue.Sell);

			System.out.println("Remove order #1");
			contract.cancelOrder(ORDER_ID_1, SELL).get();

			printQueueStatus(contract, Queue.Sell);

			assertEquals(PRICE_2030, readOrder(Queue.Sell, 0, contract).price);
			assertEquals(PRICE_2015, readOrder(Queue.Sell, 1, contract).price);
		} catch (Exception e) {
			throw new RuntimeException("Exception during unit test", e);
		} finally {
			killContract(contract);
		}
	}

	private void printQueueStatus(OrderBook contract) throws InterruptedException, ExecutionException {
		printQueueStatus(contract, Queue.Sell);
		printQueueStatus(contract, Queue.Buy);
		printQueueStatus(contract, Queue.Executed);
	}
	
	private void printQueueStatus(OrderBook contract, Queue queue) {
		String queueName = "<undefined>";
		
		switch(queue) {
		case Buy: queueName = "Buy"; break;
		case Sell: queueName = "Sell"; break; 
		case Executed: queueName = "Executed"; break; 
		}

		int i = 0;
		while (true) {
			try {
				System.out.println(String.format("%s[%s] = %s", queueName, i, readOrder(queue, i++, contract)));
			} catch (Exception e) {
				System.out.println();
				return;
			}
		}
	}

	private OrderBook deployContract() throws InterruptedException, ExecutionException {
		Future<OrderBook> contractFuture = OrderBook.deploy(Web3jHelper.getWeb3j(), Alice.CREDENTIALS, GAS_PRICE_DEFAULT, GAS_LIMIT_CONTRACT_DEPLOY,
				BigInteger.ZERO, CONTRACT_SYMBOL);

		OrderBook contract = contractFuture.get();

		System.out.println(String.format("Contract deployed at addess %s", contract.getContractAddress()));

		assertEquals(CONTRACT_SYMBOL, contract.symbol().get());
		return contract;
	}
	
	private void killContract(OrderBook contract) {
		try {
			contract.kill().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private P_Order readOrder(Queue queue, int index, OrderBook contract) throws InterruptedException, ExecutionException {
		switch(queue) {
		case Buy: return new P_Order(contract.buyOrders(new Uint256(BigInteger.valueOf(index))).get());
		case Sell: return new P_Order(contract.sellOrders(new Uint256(BigInteger.valueOf(index))).get());
		default:
			return new P_Order(contract.executedOrders(new Uint256(BigInteger.valueOf(index))).get());
		}
	}

	private class P_Order {
		public Uint256 quantity;
		public Uint256 price;
		public Bool type;
		public Address address;
		public Uint256 id;

		@SuppressWarnings("rawtypes")
		P_Order(List<Type> list) {
			quantity = (Uint256) list.get(0);
			price = (Uint256) list.get(1);
			type = (Bool) list.get(2);
			address = (Address) list.get(3);
			id = (Uint256) list.get(4);
		}

		@Override
		public String toString() {
			return String.format("{quantity:%s, price:%s, type:%s, address:%s, id:%s}", 
					quantity.getValue(),
					price.getValue(),
					type.getValue() ? "BUY" : "SELL",
					address.getValue(),
					id.getValue());
		}
	}
}
