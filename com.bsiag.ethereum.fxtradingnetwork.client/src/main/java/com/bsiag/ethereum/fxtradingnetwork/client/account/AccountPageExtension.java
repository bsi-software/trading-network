package com.bsiag.ethereum.fxtradingnetwork.client.account;

import java.util.List;

import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonNodePage;
import org.eclipse.scout.rt.client.extension.ui.desktop.outline.pages.AbstractPageWithNodesExtension;
import org.eclipse.scout.rt.client.extension.ui.desktop.outline.pages.PageWithNodesChains.PageWithNodesCreateChildPagesChain;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;

public class AccountPageExtension extends AbstractPageWithNodesExtension<PersonNodePage> {

  public AccountPageExtension(PersonNodePage owner) {
    super(owner);
  }

  @Override
  public void execCreateChildPages(PageWithNodesCreateChildPagesChain chain, List<IPage<?>> pageList) {
    super.execCreateChildPages(chain, pageList);
    AccountTablePage page = new AccountTablePage();
    page.setPersonId(getOwner().getPersonId());
    pageList.add(page);
  }

}
