package org.eclipse.scout.tradingnetwork.client.order;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import org.eclipse.scout.tradingnetwork.client.ClientSession;
import org.eclipse.scout.tradingnetwork.shared.order.IDealService;
import org.eclipse.scout.tradingnetwork.shared.order.OwnDealsTablePageData;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationService;

@Data(OwnDealsTablePageData.class)
public class OwnDealsTablePage extends AbstractDealsTablePage<OwnDealsTablePage.Table> {
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("OwnDeals");
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IDealService.class).getOwnTableData(filter));
  }

  @Override
  protected void execInitPage() {
    IOrganizationService service = BEANS.get(IOrganizationService.class);
    String ownOrganisationId = service.getOrganizationIdForUser(ClientSession.get().getUserId());
    setOrganizationId(ownOrganisationId);
    super.execInitPage();
  }

  public class Table extends AbstractDealsTablePage<OwnDealsTablePage.Table>.Table {

  }
}
