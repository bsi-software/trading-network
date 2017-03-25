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
package com.bsiag.ethereum.fxtradingnetwork.client.contact;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

import com.bsiag.ethereum.fxtradingnetwork.client.Icons;
import com.bsiag.ethereum.fxtradingnetwork.client.account.AccountTablePage;
import com.bsiag.ethereum.fxtradingnetwork.client.order.DealsTablePage;
import com.bsiag.ethereum.fxtradingnetwork.client.organization.OrganizationTablePage;
import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonTablePage;

public class ContactOutline extends AbstractOutline {

  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    // pages to be shown in the navigation area of this outline
    pageList.add(new PersonTablePage());
    pageList.add(new OrganizationTablePage());
    pageList.add(new AccountTablePage());
    pageList.add(new DealsTablePage());
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Contacts");
  }

  @Override
  protected String getConfiguredIconId() {
    return Icons.Category;
  }
}
