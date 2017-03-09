/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package com.bsiag.ethereum.fxtradingnetwork.events.client;

import java.util.Set;

import org.eclipse.scout.rt.client.extension.ui.action.menu.AbstractMenuExtension;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;

import com.bsiag.ethereum.fxtradingnetwork.client.ClientSession;
import com.bsiag.ethereum.fxtradingnetwork.client.Desktop;
import com.bsiag.ethereum.fxtradingnetwork.events.client.event.DealForm;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationService;

public class DesktopQuickAccessMenuExtension extends AbstractMenuExtension<Desktop.QuickAccessMenu> {

  public DesktopQuickAccessMenuExtension(Desktop.QuickAccessMenu owner) {
    super(owner);
  }

  @Order(30)
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
