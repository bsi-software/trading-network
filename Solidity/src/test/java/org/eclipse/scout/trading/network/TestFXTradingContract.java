package org.eclipse.scout.trading.network;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class TestFXTradingContract {
	
	private static final Utf8String COMPANY_1 = new Utf8String("Firma1");
	private static final Bool BUY = new Bool(true);	
	private static final Bool SELL = new Bool(false);
	// use localhost if a local geth client is running or replace with the docker container IP: docker inspect <containerId> | grep IPAddress
	private static final String CLIENT_IP = "172.17.0.2";
	private static final String CLIENT_PORT = "8545";
	
	private static final BigInteger GAS_PRICE_DEFAULT = BigInteger.valueOf(20_000_000_000L);
	private static final BigInteger INITIAL_VALUE_DEFAULT = BigInteger.valueOf(2_000_000L);
	private static final BigInteger GAS_LIMIT_CONTRACT_DEPLOY = BigInteger.valueOf(3_000_000L);
	
	private static final Uint256 QUANTITY_100 = new Uint256(BigInteger.valueOf(100));
	private static final Uint256 QUANTITY_200 = new Uint256(BigInteger.valueOf(200));
	private static final Uint256 PRICE_2015 = new Uint256(BigInteger.valueOf(2015));
	private static final Uint256 PRICE_2020 = new Uint256(BigInteger.valueOf(2020));
	private static final Uint256 PRICE_2025 = new Uint256(BigInteger.valueOf(2025));
	private static final Uint256 PRICE_2030 = new Uint256(BigInteger.valueOf(2030));
	
	private static final Uint256 DEAL_NR_1 = new Uint256(BigInteger.ONE);
	
	private static final Utf8String CONTRACT_SYMBOL = new Utf8String("CHFEUR");
	
	@BeforeClass
	public static void setUp() {
		try {
//			String clientUrl = String.format("http://%s:%s", CLIENT_IP, CLIENT_PORT);
//			web3j = Web3j.build(new HttpService(clientUrl));
//			Web3ClientVersion versionResponse = web3j.web3ClientVersion().sendAsync().get();
//			System.out.println(String.format("Using version %s", versionResponse.getResult()));
//			
			String ownerAddress = Alice.ADDRESS;
			String coinbaseAddress = Web3jUtil.getAccount(0);
			BigDecimal coinbaseBalance = Web3jUtil.getBalance(coinbaseAddress, Unit.ETHER);
			BigDecimal ownerBalance = Web3jUtil.getBalance(ownerAddress, Unit.ETHER);
			System.out.println(String.format("The ballance of the coinbase %s is %s Ether", coinbaseAddress, coinbaseBalance));
			System.out.println(String.format("The ballance of the contact owner %s is %s Ether", ownerAddress, ownerBalance));

			if (ownerBalance.compareTo(BigDecimal.TEN) < 0) {
				String txHash = Web3jUtil.transfer(coinbaseAddress, ownerAddress, 10, Unit.ETHER);
				System.out.println(String.format("10 ether to owner sent, txHash=%s", txHash));
			}
		} catch (Exception e) {
			throw new RuntimeException("could not set up unit test", e);
		}
	}
	
	@Test
	public void testConnection() {
		String version = Web3jUtil.getClientVersion();
		Assert.assertNotNull("web3j is null", Web3jUtil.getWeb3j());
		Assert.assertNotEquals("unexpected ethereum client version: ", "<undefined>", version);
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
	public FXTrading testDealOrdering() {
		try {
			FXTrading contract = deployFXTradingContract();

			contract.createDeal(QUANTITY_100, PRICE_2030, SELL, COMPANY_1).get();
			contract.createDeal(QUANTITY_100, PRICE_2025, SELL, COMPANY_1).get();
			contract.createDeal(QUANTITY_200, PRICE_2030, SELL, COMPANY_1).get();

			assertEquals(PRICE_2030, readDeal(false, 0, contract).price);
			assertEquals(PRICE_2030, readDeal(false, 1, contract).price);
			assertEquals(PRICE_2025, readDeal(false, 2, contract).price);

			contract.createDeal(QUANTITY_100, PRICE_2015, BUY, COMPANY_1).get();
			contract.createDeal(QUANTITY_200, PRICE_2020, BUY, COMPANY_1).get();
			contract.createDeal(QUANTITY_200, PRICE_2015, BUY, COMPANY_1).get();

			assertEquals(PRICE_2015, readDeal(true, 0, contract).price);
			assertEquals(PRICE_2015, readDeal(true, 1, contract).price);
			assertEquals(PRICE_2020, readDeal(true, 2, contract).price);
			
			return contract;
		} catch (Exception e) {
			throw new RuntimeException("Exception during unit test", e);
		}
	}
	
	@Test
	public void testDealMatcher() {
		FXTrading contract = testDealOrdering();
		
		try {
			TransactionReceipt txReceipt = contract.currentMatch().get();
		} catch (Exception e) {
			throw new RuntimeException("Exception during unit test", e);
		}
		
	}

	@Test
	public void testDealRemoving() {
		try {
			FXTrading contract = deployFXTradingContract();
			
			contract.createDeal(QUANTITY_100, PRICE_2025, SELL, COMPANY_1).get();
			contract.createDeal(QUANTITY_100, PRICE_2030, SELL, COMPANY_1).get();
			contract.createDeal(QUANTITY_200, PRICE_2015, SELL, COMPANY_1).get();
			
			printQueueStatus(contract, false);
			
			System.out.println("Remove deal 1");
			contract.revokeDeal(DEAL_NR_1, SELL).get();
			
			printQueueStatus(contract, false);
			
			assertEquals(PRICE_2025, readDeal(false, 0, contract).price);
			assertEquals(PRICE_2015, readDeal(false, 1, contract).price);
			
			// FIXME: fix smart contract so index 2 throws an ExecuteException and test it
			// assertEquals(PRICE_2015, readDeal(false, 2, contract).price);
		} catch (Exception e) {
			throw new RuntimeException("Exception during unit test", e);
		}

	}

	private void printQueueStatus(FXTrading contract, boolean buy) throws InterruptedException, ExecutionException {
		int i = 0;
		System.out.println("Queue status");
		while (true) {
			try {
				System.out.println(String.format("Deal at index %s = %s", i, readDeal(buy, i++, contract)));
			} catch (ExecutionException e) {
				System.out.println("End of queue");
				return;
			}
		}
	}

	private FXTrading deployFXTradingContract() throws InterruptedException, ExecutionException {
		Future<FXTrading> contractFuture = FXTrading.deploy(Web3jUtil.getWeb3j(), Alice.CREDENTIALS, GAS_PRICE_DEFAULT, GAS_LIMIT_CONTRACT_DEPLOY,
				BigInteger.ZERO, CONTRACT_SYMBOL);
		
		FXTrading contract = contractFuture.get();
		
		System.out.println(String.format("Contract deployed at addess %s", contract.getContractAddress()));

		assertEquals(CONTRACT_SYMBOL, contract.currencyPair().get());
		return contract;
	}

	private P_Deal readDeal(boolean buy, int index, FXTrading contract) throws InterruptedException, ExecutionException {
		return buy ? new P_Deal(contract.buyDeals(new Uint256(BigInteger.valueOf(index))).get())
				: new P_Deal(contract.sellDeals(new Uint256(BigInteger.valueOf(index))).get());
	}

	private class P_Deal {
		public Uint256 quantity;
		public Uint256 price;
		public Bool buy;
		public Utf8String company;
		public Uint256 dealNr;

		@SuppressWarnings("rawtypes")
		P_Deal(List<Type> list) {
			quantity = (Uint256) list.get(0);
			price = (Uint256) list.get(1);
			buy = (Bool) list.get(2);
			company = (Utf8String) list.get(3);
			dealNr = (Uint256) list.get(4);
		}
		
		@Override
		public String toString() {
			return P_Deal.class.getSimpleName() +
					"[quantity=" + quantity.getValue() +
					" price=" + price.getValue() +
					" buy=" + buy.getValue() +
					" company=" + company.getValue() + 
					" dealNr=" + dealNr.getValue() + "]";
		}
	}

}
