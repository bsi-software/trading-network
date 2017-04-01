package org.eclipse.scout.tradingnetwork.shared.organization;

import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationOverviewData;

/**
 * <h3>{@link IOrganizationOverviewPageService}</h3>
 *
 * @author uko
 */
@TunnelToServer
public interface IOrganizationOverviewPageService extends IService {

  /**
   * @param overviewData
   * @return
   * @throws ProcessingException
   */
  OrganizationOverviewData loadPageData(OrganizationOverviewData overviewData);

}
