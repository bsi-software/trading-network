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
package com.bsiag.ethereum.fxtradingnetwork.server.organization;

import java.util.UUID;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.holders.StringHolder;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;

import com.bsiag.ethereum.fxtradingnetwork.server.sql.SQLs;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationService;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationCreatePermission;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationFormData;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationReadPermission;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationUpdatePermission;

// tag::getTableData[]
// tag::all[]
public class OrganizationService implements IOrganizationService {
  // end::all[]

  @Override
  public OrganizationTablePageData getOrganizationTableData(SearchFilter filter) {
    OrganizationTablePageData pageData = new OrganizationTablePageData();
    // end::getTableData[]
    // tag::allOrgs[]

    String sql = SQLs.ORGANIZATION_PAGE_SELECT + SQLs.ORGANIZATION_PAGE_DATA_SELECT_INTO; // <1>
    SQL.selectInto(sql, new NVPair("page", pageData)); // <2>

    // end::allOrgs[]
    // tag::getTableData[]
    return pageData;
  }
  // end::getTableData[]
  // tag::all[]

  @Override
  public OrganizationFormData create(OrganizationFormData formData) {
    if (!ACCESS.check(new OrganizationCreatePermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    if (StringUtility.isNullOrEmpty(formData.getOrganizationId())) {
      formData.setOrganizationId(UUID.randomUUID().toString());
    }

    SQL.insert(SQLs.ORGANIZATION_INSERT, formData);

    return store(formData);
  }

  @Override
  public OrganizationFormData load(OrganizationFormData formData) {
    if (!ACCESS.check(new OrganizationReadPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    SQL.selectInto(SQLs.ORGANIZATION_SELECT, formData);

    return formData;
  }

  @Override
  public OrganizationFormData store(OrganizationFormData formData) {
    if (!ACCESS.check(new OrganizationUpdatePermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    SQL.update(SQLs.ORGANIZATION_UPDATE, formData);

    return formData;
  }
  //tag::getTableData[]

  @Override
  public String getOrganizationIdForUser(String userId) {
    if (!ACCESS.check(new OrganizationReadPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    StringHolder organizationIdHolder = new StringHolder();

    SQL.selectInto(SQLs.ORGANIZATION_FOR_USER_SELECT
    		, new NVPair("userId", userId)
    		, new NVPair("organizationId", organizationIdHolder));

    return organizationIdHolder.getValue();
  }
}
//end::getTableData[]
//end::all[]
