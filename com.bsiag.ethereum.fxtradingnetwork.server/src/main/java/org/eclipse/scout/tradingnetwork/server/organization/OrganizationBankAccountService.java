package org.eclipse.scout.tradingnetwork.server.organization;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationBankAccountService;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationBankAccountFormData;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationBankAccountTablePageData;

public class OrganizationBankAccountService implements IOrganizationBankAccountService {

  @Override
  public OrganizationBankAccountTablePageData getOrganizationBankAccountTableData(SearchFilter filter, String organizationId) {
    OrganizationBankAccountTablePageData pageData = new OrganizationBankAccountTablePageData();
    if (!StringUtility.isNullOrEmpty(organizationId)) {
      SQL.selectInto(""
          + " SELECT "
          + "     BA.ORGANIZATION_ID, "
          + "     BA.CURRENCY_UID, "
          + "     BA.BALANCE "
          + " FROM BANK_ACCOUNT BA "
          + " WHERE BA.ORGANIZATION_ID = :orgId "
          + " INTO "
          + "     :{organizationId}, "
          + "     :{currency}, "
          + "     :{balance} ", new NVPair("orgId", organizationId), pageData);
    }
    return pageData;
  }

  @Override
  public OrganizationBankAccountFormData prepareCreate(OrganizationBankAccountFormData formData) {
    return formData;
  }

  @Override
  public OrganizationBankAccountFormData create(OrganizationBankAccountFormData formData) {
    return store(formData);
  }

  @Override
  public OrganizationBankAccountFormData load(OrganizationBankAccountFormData formData) {
    SQL.selectInto(""
        + " SELECT BA.BALANCE "
        + " FROM BANK_ACCOUNT BA "
        + " WHERE ORGANIZATION_ID = :organizationId "
        + "   AND CURRENCY_UID = :currency "
        + " INTO :balance ", formData);
    return formData;
  }

  @Override
  public OrganizationBankAccountFormData store(OrganizationBankAccountFormData formData) {
    int updateCount = SQL.update(""
        + " UPDATE BANK_ACCOUNT SET BALANCE = :balance "
        + " WHERE ORGANIZATION_ID = :organizationId AND CURRENCY_UID = :currency ", formData);
    if (updateCount <= 0) {
      SQL.insert(""
          + " INSERT "
          + " INTO BANK_ACCOUNT (ORGANIZATION_ID, CURRENCY_UID, BALANCE)"
          + " VALUES (:organizationId, :currency, :balance) ", formData);
    }
    return formData;
  }
}
