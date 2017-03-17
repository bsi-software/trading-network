package com.bsiag.ethereum.fxtradingnetwork.events.client.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICode;

import com.bsiag.ethereum.fxtradingnetwork.client.ClientSession;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.OrderBookTypeCodeType;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationService;

public class NetworkNodePage extends AbstractPageWithNodes {

  private String m_organizationId;

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Network");
  }

  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    pageList.addAll(getOrderBookTypeNetworkPages());
  }

  private List<IPage<?>> getOrderBookTypeNetworkPages() {
    List<? extends ICode<String>> codes = BEANS.get(OrderBookTypeCodeType.class).getCodes();
    List<IPage<?>> networkPages = new ArrayList<IPage<?>>();
    for (ICode<String> code : codes) {
      NetworkTablePage networkPage = new NetworkTablePage(getOrganizationId(), code.getId());
      networkPages.add(networkPage);
    }
    return networkPages;
  }

  private String getOrganizationId() {
    String organizationId = m_organizationId;
    if (StringUtility.isNullOrEmpty(organizationId)) {
      organizationId = BEANS.get(IOrganizationService.class).getOrganizationIdForUser(ClientSession.get().getUserId());
    }
    return organizationId;
  }
}
