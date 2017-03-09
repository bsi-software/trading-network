package com.bsiag.ethereum.fxtradingnetwork.events.client.event;

import java.util.List;

import org.eclipse.scout.rt.client.extension.ui.desktop.outline.pages.AbstractPageWithNodesExtension;
import org.eclipse.scout.rt.client.extension.ui.desktop.outline.pages.PageWithNodesChains.PageWithNodesCreateChildPagesChain;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;

import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationNodePage;
import com.bsiag.ethereum.fxtradingnetwork.events.client.organization.OrganizationBankAccountTablePage;

/**
 * <h3>{@link EventPageExtension}</h3>
 *
 * @author mzi
 */
public class EventPageExtension extends AbstractPageWithNodesExtension<OrganizationNodePage> {

  public EventPageExtension(OrganizationNodePage owner) {
    super(owner);
  }

  @Override
  public void execCreateChildPages(PageWithNodesCreateChildPagesChain chain, List<IPage<?>> pageList) {
    super.execCreateChildPages(chain, pageList);
    pageList.add(new DealsTablePage());
    pageList.add(new OrganizationBankAccountTablePage());
  }
}
