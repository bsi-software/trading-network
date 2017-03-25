package com.bsiag.ethereum.fxtradingnetwork.shared.account;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

/**
 * <h3>{@link IAccountService}</h3>
 *
 * @author mzi
 */
@TunnelToServer
public interface IAccountService extends IService {

  /**
   * @param filter
   * @param personId
   * @return
   */
  AccountTablePageData getAccountTableData(SearchFilter filter, String personId);

  /**
   * @param formData
   * @return
   */
  AccountFormData prepareCreate(AccountFormData formData);

  /**
   * @param formData
   * @return
   */
  AccountFormData create(AccountFormData formData);

  /**
   * @param formData
   * @return
   */
  AccountFormData load(AccountFormData formData);

  /**
   * @param formData
   * @return
   */
  AccountFormData store(AccountFormData formData);

  /**
   * @param accountTo
   * @return
   */
  String getPerson(String accountTo);
}
