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
package org.eclipse.scout.tradingnetwork.server.person;

import java.util.UUID;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.eclipse.scout.tradingnetwork.server.ethereum.EthereumService;
import org.eclipse.scout.tradingnetwork.server.ethereum.model.Account;
import org.eclipse.scout.tradingnetwork.server.sql.SQLs;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountFormData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountTablePageData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.AccountTablePageData.AccountTableRowData;
import org.eclipse.scout.tradingnetwork.shared.ethereum.IAccountService;
import org.eclipse.scout.tradingnetwork.shared.person.IPersonService;
import org.eclipse.scout.tradingnetwork.shared.person.PersonCreatePermission;
import org.eclipse.scout.tradingnetwork.shared.person.PersonFormData;
import org.eclipse.scout.tradingnetwork.shared.person.PersonReadPermission;
import org.eclipse.scout.tradingnetwork.shared.person.PersonSearchFormData;
import org.eclipse.scout.tradingnetwork.shared.person.PersonTablePageData;
import org.eclipse.scout.tradingnetwork.shared.person.PersonUpdatePermission;

public class PersonService implements IPersonService {

  @Override
  public PersonTablePageData getPersonTableData(SearchFilter filter, String organizationId) {
    PersonTablePageData pageData = new PersonTablePageData();
    PersonSearchFormData searchData = (PersonSearchFormData) filter.getFormData();
    StringBuilder sql = new StringBuilder();

    sql.append(SQLs.PERSON_PAGE_SELECT);
    sql.append(" WHERE 1 = 1 ");
    addToWhere(sql, organizationId, "organization_id", "organizationId");

    if (searchData != null) {
      addToWhere(sql, searchData.getFirstName().getValue(), "first_name", "firstName");
      addToWhere(sql, searchData.getLastName().getValue(), "last_name", "lastName");
      addToWhere(sql, searchData.getLocation().getCity().getValue(), "city", "location.city");
      addToWhere(sql, searchData.getLocation().getCountry().getValue(), "country", "location.country");
      addToWhere(sql, searchData.getOrganization().getValue(), "organization_id", "organization");
    }

    sql.append(SQLs.PERSON_PAGE_DATA_SELECT_INTO);

    SQL.selectInto(sql.toString(), searchData, new NVPair("organizationId", organizationId), new NVPair("page", pageData));

    return pageData;
  }

  protected void addToWhere(StringBuilder sqlWhere, String fieldValue, String sqlAttribute, String searchAttribute) {
    if (StringUtility.hasText(fieldValue)) {
      sqlWhere.append(String.format(SQLs.AND_LIKE_CAUSE, sqlAttribute, searchAttribute));
    }
  }

  @Override
  public PersonFormData create(PersonFormData formData) {
    if (!ACCESS.check(new PersonCreatePermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    if (StringUtility.isNullOrEmpty(formData.getPersonId())) {
      formData.setPersonId(UUID.randomUUID().toString());
    }

    SQL.insert(SQLs.PERSON_INSERT, formData);

    return store(formData);
  }

  @Override
  public PersonFormData load(PersonFormData formData) {
    if (!ACCESS.check(new PersonReadPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    SQL.selectInto(SQLs.PERSON_SELECT, formData);

    String personId = formData.getPersonId();
    Account wallet = null;

    IAccountService accountService = BEANS.get(IAccountService.class);
    AccountTablePageData pageData = accountService.getAccountTableData(new SearchFilter(), personId);
    if (pageData.getRowCount() > 0) {
      AccountTableRowData firstRow = pageData.rowAt(0);
      AccountFormData accountData = accountService.load(firstRow.getAddress());
      wallet = BEANS.get(EthereumService.class).getWallet(accountData.getAddress().getValue(), accountData.getPassword().getValue());
    }

    if (null != wallet) {
      formData.getWalletAddress().setValue(wallet.getAddress());
      formData.getWalletPath().setValue(wallet.getFile().getAbsolutePath());
    }

    return formData;
  }

  @Override
  public PersonFormData store(PersonFormData formData) {
    if (!ACCESS.check(new PersonUpdatePermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    SQL.update(SQLs.PERSON_UPDATE, formData);

    return formData;
  }
}
// end::all[]
// end::getTableData[]
