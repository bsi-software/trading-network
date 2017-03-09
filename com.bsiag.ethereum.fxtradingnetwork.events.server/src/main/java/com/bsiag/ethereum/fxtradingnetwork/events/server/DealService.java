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
package com.bsiag.ethereum.fxtradingnetwork.events.server;

import java.util.UUID;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.eclipse.scout.rt.shared.session.Sessions;

import com.bsiag.ethereum.fxtradingnetwork.events.server.sql.SQLs;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.StatusCodeType;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.CreateEventPermission;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.DealFormData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.DealsTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.IDealService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.OwnDealsTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.ReadEventPermission;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.UpdateDealPermission;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationService;

public class DealService implements IDealService {

  @Override
  public DealsTablePageData getTableData(SearchFilter filter, String organizationId) {
    DealsTablePageData pageData = new DealsTablePageData();

    StringBuilder sqlSelect = new StringBuilder(SQLs.DEAL_PAGE_DATA_SELECT);
    StringBuilder sqlWhere = new StringBuilder(" WHERE 1 = 1 ");

    if (StringUtility.hasText(organizationId)) {
      sqlWhere.append(SQLs.DEAL_PAGE_DATA_WHERE_CLAUSE);
    }

    String sql = sqlSelect.append(sqlWhere).append(SQLs.DEAL_PAGE_DATA_INTO).toString();

    SQL.selectInto(sql, new NVPair("organizationId", organizationId), new NVPair("page", pageData));

    return pageData;
  }

  @Override
  public OwnDealsTablePageData getOwnTableData(SearchFilter filter) {
    OwnDealsTablePageData pageData = new OwnDealsTablePageData();

    String userId = Sessions.currentSession(ISession.class).getUserId();
    String userOrganizationId = BEANS.get(IOrganizationService.class).getOrganizationIdForUser(userId);

    if (!StringUtility.isNullOrEmpty(userId) && !StringUtility.isNullOrEmpty(userOrganizationId)) {
      StringBuilder sqlSelect = new StringBuilder(SQLs.DEAL_PAGE_DATA_SELECT);
      StringBuilder sqlWhere = new StringBuilder(" WHERE 1 = 1 ");

      sqlWhere.append(SQLs.DEAL_PAGE_DATA_WHERE_CLAUSE);

      String sql = sqlSelect.append(sqlWhere).append(SQLs.DEAL_PAGE_DATA_INTO).toString();

      SQL.selectInto(sql, new NVPair("organizationId", userOrganizationId), new NVPair("page", pageData));
    }

    return pageData;
  }

  @Override
  public DealFormData create(DealFormData formData) {
    if (!ACCESS.check(new CreateEventPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    if (StringUtility.isNullOrEmpty(formData.getDealId())) {
      formData.setDealId(UUID.randomUUID().toString());
      formData.setDealNr(UUID.randomUUID().toString());
      formData.setStatus(StatusCodeType.InactiveCode.ID);
    }

    SQL.insert(SQLs.DEAL_INSERT, formData);

    return store(formData);
  }

  @Override
  public DealFormData load(DealFormData formData) {
    if (!ACCESS.check(new ReadEventPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    SQL.selectInto(SQLs.DEAL_SELECT, formData);
//    SQL.selectInto(SQLs.EVENT_PARTICIPANTS_SELECT, formData);

    return formData;
  }

  @Override
  public DealFormData prepareCreate(DealFormData formData) {
    if (!ACCESS.check(new CreateEventPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    return formData;
  }

  @Override
  public DealFormData store(DealFormData formData) {
    if (!ACCESS.check(new UpdateDealPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    SQL.update(SQLs.DEAL_UPDATE, formData);

//    TableBeanHolderFilter deletedParticipants = new TableBeanHolderFilter(formData.getParticipantTableField(), ITableHolder.STATUS_DELETED);
//    TableBeanHolderFilter insertedParticipants = new TableBeanHolderFilter(formData.getParticipantTableField(), ITableHolder.STATUS_INSERTED);
    // NVPair dealId = new NVPair("dealId", formData.getDealId());

//    SQL.delete(SQLs.EVENT_PARTICIPANTS_DELETE, deletedParticipants, eventId);
//    SQL.insert(SQLs.EVENT_PARTICIPANTS_INSERT, insertedParticipants, eventId);

    return formData;
  }
}
