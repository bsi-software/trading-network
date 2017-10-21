package org.eclipse.scout.tradingnetwork.server.organization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.holders.StringHolder;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType;
import org.eclipse.scout.tradingnetwork.shared.order.StatusCodeType;
import org.eclipse.scout.tradingnetwork.shared.order.TradingActionCodeType;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationOverviewPageService;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationOverviewData;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationOverviewData.OverviewTable.OverviewTableRowData;

public class OrganizationOverviewPageService implements IOrganizationOverviewPageService {

  @Override
  public OrganizationOverviewData loadPageData(OrganizationOverviewData overviewData) {
    if (StringUtility.isNullOrEmpty(overviewData.getUserId())) {
      return overviewData;
    }

    StringHolder organizationIdHolder = new StringHolder();

    SQL.selectInto("SELECT      "
        + "             O.NAME,"
        + "             O.ORGANIZATION_ID,  "
        + "             O.COUNTRY, "
        + "             O.LOGO_URL "
        + "FROM         ORGANIZATION O "
        + "WHERE        O.USER_ID = :userId "
        + "LIMIT        1 "
        + "INTO         :organizationName, "
        + "             :organizationId, "
        + "             :country, "
        + "             :organizationLogo.url ", overviewData, new NVPair("organizationId", organizationIdHolder));

    if (StringUtility.hasText(organizationIdHolder.getValue())) {
//      OrganizationBankAccountTablePageData accountBalanceTableData = BEANS.get(IOrganizationBankAccountService.class).getOrganizationBankAccountTableData(null, organizationIdHolder.getValue());
//      for (OrganizationBankAccountTableRowData row : accountBalanceTableData.getRows()) {
//        AccountBalanceTable table = overviewData.getAccountBalanceTable();
//        AccountBalanceTableRowData addedRow = table.addRow();
//        addedRow.setOrganizationId(row.getOrganizationId());
//        addedRow.setCurrency(row.getCurrency());
//        addedRow.setBalance(row.getBalance());
//      }

      OverviewTableRowData[] overviewRows = loadOverviewTableRowData(organizationIdHolder.getValue());
      overviewData.getOverviewTable().setRows(overviewRows);
    }

    return overviewData;
  }

  private OverviewTableRowData[] loadOverviewTableRowData(String organizationId) {
    List<OverviewTableRowData> rowDatas = new ArrayList<OverviewTableRowData>();

    Object[][] data = SQL.select(""
        + " SELECT d.order_book_type, "
        + "        d.trading_action, "
        + "        d.status, "
        + "        count(d.status), "
        + "        sum(d.quantity) "
        + " FROM deal d "
        + " WHERE d.organization_id = :organizationId "
        + " GROUP BY d.order_book_type, d.trading_action, d.status "
        + "       ", new NVPair("organizationId", organizationId));

    for (Object[] rowData : data) {
      OverviewTableRowData row = new OverviewTableRowData();
      StringBuilder description = new StringBuilder();
      ICode<String> orderBooKCode = BEANS.get(OrderBookTypeCodeType.class).getCode(TypeCastUtility.castValue(rowData[0], String.class));
      if (null != orderBooKCode) {
        description.append(orderBooKCode.getText());
        description.append(" - ");
      }
      ICode<String> tradingActionCode = BEANS.get(TradingActionCodeType.class).getCode(TypeCastUtility.castValue(rowData[1], String.class));
      if (null != tradingActionCode) {
        description.append(tradingActionCode.getText());
        description.append(" - ");
      }
      ICode<String> statusCode = BEANS.get(StatusCodeType.class).getCode(TypeCastUtility.castValue(rowData[2], String.class));
      if (null != statusCode) {
        description.append(statusCode.getText());
      }

      row.setInfoType(description.toString());
      row.setCount(TypeCastUtility.castValue(rowData[3], Long.class));
      rowDatas.add(row);
    }

    //    for (ICode<String> orderBook : BEANS.get(OrderBookTypeCodeType.class).getCodes()) {
//      for (ICode<String> tradingAction : BEANS.get(TradingActionCodeType.class).getCodes()) {
//        for (ICode<String> status : BEANS.get(StatusCodeType.class).getCodes()) {
//        }
//      }
//    }
    return rowDatas.toArray(new OverviewTableRowData[rowDatas.size()]);
  }

}
