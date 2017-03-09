package com.bsiag.ethereum.fxtradingnetwork.client.person;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;

public class PersonNodePage extends AbstractPageWithNodes {

  private String personId;

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }
}
