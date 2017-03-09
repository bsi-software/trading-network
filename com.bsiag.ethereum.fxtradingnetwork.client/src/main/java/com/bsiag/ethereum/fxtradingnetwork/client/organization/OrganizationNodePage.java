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
package com.bsiag.ethereum.fxtradingnetwork.client.organization;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;

import com.bsiag.ethereum.fxtradingnetwork.client.person.PersonTablePage;

// tag::all[]
public class OrganizationNodePage extends AbstractPageWithNodes {

  private String organizationId; // <1>

  public String getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  @Override // <2>
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    PersonTablePage personTablePage = new PersonTablePage();
    personTablePage.setOrganizationId(getOrganizationId()); // <3>
    pageList.add(personTablePage);
  }
}
// end::all[]
