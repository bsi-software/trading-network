package com.bsiag.ethereum.fxtradingnetwork.events.client.event;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.bsiag.ethereum.fxtradingnetwork.client.ClientSession;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.IDealService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.OwnDealsTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationService;

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
