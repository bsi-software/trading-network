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
package com.bsiag.ethereum.fxtradingnetwork.client.order;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;

import com.bsiag.ethereum.fxtradingnetwork.client.Icons;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationOverview;
import com.bsiag.ethereum.fxtradingnetwork.client.tradingcenter.NetworkNodePage;

public class DealOutline extends AbstractOutline {

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Overview");
  }

  @Override
  protected Class<? extends IForm> getConfiguredDefaultDetailForm() {
    return OrganizationOverview.class;
  }

  @Override
  protected void execInitDefaultDetailForm() {
    getDefaultDetailForm().start();
    if (getDefaultDetailForm() instanceof OrganizationOverview) {
      String title = ((OrganizationOverview) getDefaultDetailForm()).getOrganizationName();
      if (!StringUtility.isNullOrEmpty(title)) {
        setTitle(title);
      }
    }
  }

  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    OwnDealsTablePage dealsTablePage = new OwnDealsTablePage();
    pageList.add(dealsTablePage);
    NetworkNodePage networkNodePage = new NetworkNodePage();
    pageList.add(networkNodePage);
  }

  @Override
  protected String getConfiguredIconId() {
    return Icons.World;
  }
}
