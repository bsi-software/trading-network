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
package com.bsiag.ethereum.fxtradingnetwork.events.client.event;

import org.eclipse.scout.rt.client.dto.PageData;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationNodePage;
import com.bsiag.ethereum.fxtradingnetwork.events.client.Icons;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.DealsTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.IDealService;

@PageData(DealsTablePageData.class)
public class DealsTablePage extends AbstractDealsTablePage<DealsTablePage.Table> {

  @Override
  protected String getConfiguredIconId() {
    return Icons.World;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("OwnDeals");
  }

  @Override
  protected boolean getConfiguredLeaf() {
    return true;
  }

  @Override
  protected void execInitPage() {
    OrganizationNodePage organizationParentPage = getParentNode(OrganizationNodePage.class);

    if (organizationParentPage != null) {
      setOrganizationId(organizationParentPage.getOrganizationId());
    }
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IDealService.class).getTableData(filter, getOrganizationId()));
  }

  public class Table extends AbstractDealsTablePage<DealsTablePage.Table>.Table {

  }

}
