package org.eclipse.scout.tradingnetwork.client;

import java.util.Set;

import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;

import org.eclipse.scout.tradingnetwork.client.order.DealForm;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationService;

public class AbstractQuickAccessMenu extends AbstractMenu {
  @Override
  protected String getConfiguredText() {
    return TEXTS.get("QuickAccess");
  }

  @Override
  protected String getConfiguredKeyStroke() {
    return IKeyStroke.F10;
  }

  @Order(1000)
  public class NewDealMenu extends AbstractMenu {
    String ownOrganisationId;

    @Override
    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
      return CollectionUtility.<IMenuType> hashSet();
    }

    @Override
    protected String getConfiguredText() {
      return TEXTS.get("CreateNewDeal");
    }

    @Override
    protected void execInitAction() {
      IOrganizationService service = BEANS.get(IOrganizationService.class);
      ownOrganisationId = service.getOrganizationIdForUser(ClientSession.get().getUserId());

    }

    @Override
    protected void execAction() {
      final DealForm form = new DealForm();
      form.startNew();
      form.setOrganizationId(ownOrganisationId);
    }
  }
}
