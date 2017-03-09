package com.bsiag.ethereum.fxtradingnetwork.events.shared.organization;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

/**
 * <h3>{@link IOrganizationBankAccountService}</h3>
 *
 * @author uko
 */
@TunnelToServer
public interface IOrganizationBankAccountService extends IService {

  /**
   * @param filter
   * @return
   */
  OrganizationBankAccountTablePageData getOrganizationBankAccountTableData(SearchFilter filter, String organizationId);

  /**
   * @param formData
   * @return
   */
  OrganizationBankAccountFormData prepareCreate(OrganizationBankAccountFormData formData);

  /**
   * @param formData
   * @return
   */
  OrganizationBankAccountFormData create(OrganizationBankAccountFormData formData);

  /**
   * @param formData
   * @return
   */
  OrganizationBankAccountFormData load(OrganizationBankAccountFormData formData);

  /**
   * @param formData
   * @return
   */
  OrganizationBankAccountFormData store(OrganizationBankAccountFormData formData);
}
