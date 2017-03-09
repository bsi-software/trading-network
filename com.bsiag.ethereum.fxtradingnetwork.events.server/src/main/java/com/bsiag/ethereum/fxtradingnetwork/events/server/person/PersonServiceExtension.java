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
package com.bsiag.ethereum.fxtradingnetwork.events.server.person;

import java.util.HashMap;
import java.util.Map;

import com.bsiag.ethereum.fxtradingnetwork.events.account.EthereumService;
import com.bsiag.ethereum.fxtradingnetwork.events.account.model.Account;
import com.bsiag.ethereum.fxtradingnetwork.events.server.EventCountBean;
import com.bsiag.ethereum.fxtradingnetwork.events.server.sql.SQLs;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.person.PersonFormTabExtensionData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.person.PersonFormWalletTabExtensionData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.person.PersonTablePageDataExtension;
import com.bsiag.ethereum.fxtradingnetwork.server.person.PersonService;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.PersonFormData;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.PersonTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.PersonTablePageData.PersonTableRowData;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Replace;
import org.eclipse.scout.rt.platform.holders.BeanArrayHolder;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.util.NumberUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@Replace
public class PersonServiceExtension extends PersonService {

  @Override
  public PersonTablePageData getPersonTableData(SearchFilter filter, String organizationId) {
    PersonTablePageData pageData = super.getPersonTableData(filter, organizationId);

    // Add number of events to persons
    BeanArrayHolder<EventCountBean> arrayHolder = new BeanArrayHolder<>(EventCountBean.class);
    SQL.selectInto(SQLs.EVENT_COUNT_BY_PERSON, new NVPair("bean", arrayHolder));

    // Create a map to access event count by person
    Map<String, Long> eventCounts = new HashMap<>();
    for (EventCountBean counter : arrayHolder.getBeans()) {
      eventCounts.put(counter.getPersonId(), counter.getEventCount());
    }

    // Add event count to persons
    for (PersonTableRowData personRow : pageData.getRows()) {
      long eventCount = NumberUtility.nvl(eventCounts.get(personRow.getPersonId()), 0L);
      personRow.getContribution(PersonTablePageDataExtension.class).setEvents(eventCount);
    }

    return pageData;
  }

  @Override
  public PersonFormData load(PersonFormData formData) {
    formData = super.load(formData);

    PersonFormTabExtensionData extensionData = formData.getContribution(PersonFormTabExtensionData.class);
    SQL.selectInto(SQLs.PERSON_EVENT_SELECT, extensionData, formData);

    PersonFormWalletTabExtensionData walletExtensionData = formData.getContribution(PersonFormWalletTabExtensionData.class);

    // handle alice (prs01) and lena (prs01a)
    String personId = formData.getPersonId();
    Account wallet = null;

    if (personId.equals("prs01")) {
      wallet = BEANS.get(EthereumService.class).getWallet("0x8d2ec831056c620fea2fabad8bf6548fc5810cc3");
    }
    else if (personId.equals("prs01a")) {
      wallet = BEANS.get(EthereumService.class).getWallet("0xcbc12f306da804bb681aceeb34f0bc58ba2f7ad7");
    }

    if (wallet != null) {
      walletExtensionData.getWalletAddress().setValue(wallet.getAddress());
      walletExtensionData.getWalletPath().setValue(wallet.getFile().getAbsolutePath());
    }

    return formData;
  }
}
