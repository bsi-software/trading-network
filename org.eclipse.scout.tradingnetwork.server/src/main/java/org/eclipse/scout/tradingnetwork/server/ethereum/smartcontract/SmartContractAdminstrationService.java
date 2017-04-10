package org.eclipse.scout.tradingnetwork.server.ethereum.smartcontract;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationFormData;
import org.eclipse.scout.tradingnetwork.client.ethereum.smartcontract.SmartContractAdministrationTablePageData;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumProperties.EthereumClientProperty;
import org.eclipse.scout.tradingnetwork.server.orderbook.OrderBookService;
import org.eclipse.scout.tradingnetwork.server.sql.SQLs;
import org.eclipse.scout.tradingnetwork.shared.ethereum.smartcontract.ISmartContractAdminstrationService;

public class SmartContractAdminstrationService implements ISmartContractAdminstrationService {

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
