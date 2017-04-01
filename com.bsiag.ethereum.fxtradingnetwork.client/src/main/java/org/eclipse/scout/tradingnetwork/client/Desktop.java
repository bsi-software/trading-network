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
package org.eclipse.scout.tradingnetwork.client;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.keystroke.AbstractKeyStroke;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.form.AbstractFormMenu;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.config.PlatformConfigProperties.ApplicationNameProperty;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.AbstractIcons;
import org.eclipse.scout.rt.shared.ISession;

import org.eclipse.scout.tradingnetwork.client.contact.ContactOutline;
import org.eclipse.scout.tradingnetwork.client.order.DealOutline;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationService;

public class Desktop extends AbstractDesktop {
  @Override
  protected String getConfiguredTitle() {
    return CONFIG.getPropertyValue(ApplicationNameProperty.class);
  }

  @Override
  protected String getConfiguredLogoId() {
    return "application_logo";
  }

  @Override
  protected List<Class<? extends IOutline>> getConfiguredOutlines() {
    List<Class<? extends IOutline>> outlines = new ArrayList<>();
    outlines.add(DealOutline.class);
    outlines.add(ContactOutline.class);
    return outlines;
  }

  @Override
  protected void execDefaultView() {
    String organizationId = BEANS.get(IOrganizationService.class).getOrganizationIdForUser(ClientSession.get().getUserId());
    if (StringUtility.hasText(organizationId)) {
      setOutline(DealOutline.class);
    }
    else {
      setOutline(ContactOutline.class);
    }
  }

  public static Desktop get() {
    return (Desktop) ClientSessionProvider.currentSession().getDesktop();
  }

  public class RefreshOutlineKeyStroke extends AbstractKeyStroke {

    @Override
    protected String getConfiguredKeyStroke() {
      return IKeyStroke.F5;
    }

    @Override
    protected void execAction() {
      if (getOutline() != null) {
        IPage<?> page = getOutline().getActivePage();
        if (page != null) {
          page.reloadPage();
        }
      }
    }
  }

  // outline buttons of the application
  @Order(1000)
  public class ContactOutlineViewButton extends AbstractOutlineViewButton {

    public ContactOutlineViewButton() {
      this(ContactOutline.class);
    }

    protected ContactOutlineViewButton(Class<? extends ContactOutline> outlineClass) {
      super(Desktop.this, outlineClass);
    }

    @Override
    protected DisplayStyle getConfiguredDisplayStyle() {
      return DisplayStyle.MENU;
    }

    @Override
    protected String getConfiguredKeyStroke() {
      return "ctrl-shift-c";
    }
  }

  @Order(2000)
  public class EventOutlineViewButton extends AbstractOutlineViewButton {

    public EventOutlineViewButton() {
      super(Desktop.this, DealOutline.class);
    }

    @Override
    protected DisplayStyle getConfiguredDisplayStyle() {
      return DisplayStyle.MENU;
    }

    @Override
    protected String getConfiguredKeyStroke() {
      return "ctrl-shift-e";
    }
  }

  // top level menus for the header area of the application
  @Order(1000)
  public class QuickAccessMenu extends AbstractQuickAccessMenu {

  }

  @Order(2000)
  public class UserMenu extends AbstractFormMenu<UserForm> { // <2>

    @Override
    protected String getConfiguredIconId() {
      return AbstractIcons.Person;
    }

    @Override
    protected String getConfiguredKeyStroke() {
      return IKeyStroke.F12;
    }

    @Override
    protected void execInitAction() {
      setText(ISession.CURRENT.get().getUserId());
    }

    @Override
    protected Class<UserForm> getConfiguredForm() {
      return UserForm.class;
    }
  }
}
