package org.eclipse.scout.tradingnetwork.server.ethereum.smartcontract;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationFormData;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationTablePageData;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumProperties.EthereumClientProperty;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumProperties.EthereumDefaultAccount;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumService;
import org.eclipse.scout.tradingnetwork.server.ethereum.model.Account;
import org.eclipse.scout.tradingnetwork.server.orderbook.OrderBookService;
import org.eclipse.scout.tradingnetwork.server.sql.SQLs;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IAccountService;
import org.eclipse.scout.tradingnetwork.shared.ethereum.smartcontract.ISmartContractAdminstrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartContractAdminstrationService implements ISmartContractAdminstrationService {

  private static final Logger LOG = LoggerFactory.getLogger(SmartContractAdminstrationService.class);

  @Override
  public SmartContractAdministrationTablePageData loadTableData() {
    SmartContractAdministrationTablePageData pageData = new SmartContractAdministrationTablePageData();
    SQL.selectInto(SQLs.SMART_CONTRACT_SELECT_DEPLOYED_ORDER_BOOKS, pageData);

    return pageData;
  }

  @Override
  public SmartContractAdministrationFormData load(SmartContractAdministrationFormData formData) {
    SQL.selectInto(""
        + " SELECT OB.ADDRESS FROM DEPLOYED_ORDER_BOOK OB "
        + " WHERE OB.ORDER_BOOK_TYPE = :orderBookType "
        + "   AND OB.ENVIRONMENT = :environment "
        + " LIMIT 1 "
        + " INTO :address ",
        formData);
    return formData;
  }

  @Override
  public SmartContractAdministrationFormData create(SmartContractAdministrationFormData formData) {
    if (formData.getCreateContract().getValue()) {
      SmartContractAdministrationFormData testFormData = load(formData);
      if (StringUtility.hasText(testFormData.getAddress().getValue())) {
        throw new VetoException(TEXTS.get("ContractAlreadyExists"));
      }
      String contractOwnerAddress = CONFIG.getPropertyValue(EthereumDefaultAccount.class);
      if (StringUtility.isNullOrEmpty(contractOwnerAddress)) {
        throw new VetoException(TEXTS.get("SupplyContractOwnerAddress"));
      }
      String address = "";
      try {
        Account account = BEANS.get(EthereumService.class).getWallet(contractOwnerAddress, BEANS.get(IAccountService.class).getPassword(contractOwnerAddress));
        address = BEANS.get(OrderBookService.class).deploy(account.getCredentials(), EthereumService.GAS_PRICE_DEFAULT, BigInteger.valueOf(4_000_000L), formData.getEnvironment().getValue());
      }
      catch (InterruptedException | ExecutionException e) {
        LOG.error(e.getMessage());
      }

      if (StringUtility.hasText(address)) {
        formData.getAddress().setValue(address);
      }
      else {
        throw new VetoException(TEXTS.get("CouldNotDeployContract"));
      }
    }

    formData = store(formData);
    return formData;
  }

  @Override
  public SmartContractAdministrationFormData store(SmartContractAdministrationFormData formData) {
    return store(formData, false);
  }

  @Override
  public SmartContractAdministrationFormData store(SmartContractAdministrationFormData formData, boolean overwrite) {
    int updateResult = 0;

    //TODO [uko] move to SQLs class
    if (overwrite) {
      updateResult = SQL.update(""
          + " UPDATE DEPLOYED_ORDER_BOOK "
          + " SET ADDRESS = :address "
          + " WHERE ORDER_BOOK_TYPE = :orderBookType "
          + "   AND ENVIRONMENT = :environment ",
          formData);
    }

    if (updateResult == 0) {
      SQL.insert("INSERT INTO DEPLOYED_ORDER_BOOK (ENVIRONMENT, ORDER_BOOK_TYPE, ADDRESS) "
          + " VALUES (:environment, :orderBookType, :address) ",
          formData);
    }

    return formData;
  }

  @Override
  public void delete(SmartContractAdministrationFormData formData) {
    SQL.delete(""
        + " DELETE FROM DEPLOYED_ORDER_BOOK "
        + " WHERE  ORDER_BOOK_TYPE = :orderBookType "
        + " AND    ENVIRONMENT = :environment ", formData);

    if (CONFIG.getPropertyValue(EthereumClientProperty.class).equals(formData.getEnvironment().getValue())) {
      BEANS.get(OrderBookService.class).removeContractFromCache(formData.getOrderBookType().getValue());
    }
  }

  @Override
  public void delete(SmartContractAdministrationFormData[] formDatas) {
    for (SmartContractAdministrationFormData formData : formDatas) {
      delete(formData);
    }
  }

}
