package org.eclipse.scout.tradingnetwork.server.organization;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.holders.StringHolder;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;

import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationBankAccountService;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationOverviewPageService;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationBankAccountTablePageData;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationOverviewData;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationBankAccountTablePageData.OrganizationBankAccountTableRowData;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationOverviewData.AccountBalanceTable;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationOverviewData.AccountBalanceTable.AccountBalanceTableRowData;

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
      OrganizationBankAccountTablePageData accountBalanceTableData = BEANS.get(IOrganizationBankAccountService.class).getOrganizationBankAccountTableData(null, organizationIdHolder.getValue());
      for (OrganizationBankAccountTableRowData row : accountBalanceTableData.getRows()) {
        AccountBalanceTable table = overviewData.getAccountBalanceTable();
        AccountBalanceTableRowData addedRow = table.addRow();
        addedRow.setOrganizationId(row.getOrganizationId());
        addedRow.setCurrency(row.getCurrency());
        addedRow.setBalance(row.getBalance());
      }

    }

    return overviewData;
  }

}
