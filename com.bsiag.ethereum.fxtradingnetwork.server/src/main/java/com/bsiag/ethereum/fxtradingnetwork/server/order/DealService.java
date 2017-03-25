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
package com.bsiag.ethereum.fxtradingnetwork.server.order;

import java.math.BigDecimal;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.eclipse.scout.rt.shared.session.Sessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsiag.ethereum.fxtradingnetwork.server.orderbook.OrderBookService;
import com.bsiag.ethereum.fxtradingnetwork.server.orderbook.model.Order;
import com.bsiag.ethereum.fxtradingnetwork.server.sql.SQLs;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.CreateEventPermission;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.DealFormData;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.DealsTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.IDealService;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.OwnDealsTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.ReadEventPermission;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.StatusCodeType;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.TradingActionCodeType;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.UpdateDealPermission;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.IOrganizationService;

public class DealService implements IDealService {

  private static final Logger LOG = LoggerFactory.getLogger(DealService.class);

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

    if (CompareUtility.isOneOf(formData.getDealId(), null, 0L)) {
      formData.setDealId(SQL.getSequenceNextval("deal_deal_id_seq"));
      formData.setStatus(StatusCodeType.InactiveCode.ID);
    }

    SQL.insert(SQLs.DEAL_INSERT, formData);

    return store(formData);
  }

  @Override
  public boolean publish(Long dealId) throws ProcessingException {
    boolean success = true;
    DealFormData formData = new DealFormData();
    formData.setDealId(dealId);
    formData = load(formData);
    try {
      String transactionHash = BEANS.get(OrderBookService.class).publish(convertToOrder(formData));
      //TODO: [uko] change status to pending and save hash
      if (StringUtility.hasText(transactionHash)) {
        formData.setStatus(StatusCodeType.PublishedCode.ID);
        store(formData);
      }
    }
    catch (Exception e) {
      LOG.error(e.getMessage());
      throw new ProcessingException(e.getMessage());
    }
    return success;
  }

  @Override
  public DealFormData load(DealFormData formData) {
    if (!ACCESS.check(new ReadEventPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    SQL.selectInto(SQLs.DEAL_SELECT, formData);

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

    return formData;
  }

  @Override
  public BigDecimal getCurrentExchangeRate(String orderBookId, String tradingActionId) throws ProcessingException {
    Order topOrderInverse = null;
    OrderBookService service = BEANS.get(OrderBookService.class);
    if (TradingActionCodeType.BuyCode.ID.equals(tradingActionId)) {
      topOrderInverse = service.getTopSellOrder(orderBookId);
    }
    else if (TradingActionCodeType.SellCode.ID.equals(tradingActionId)) {
      topOrderInverse = service.getTopBuyOrder(orderBookId);
    }
    BigDecimal exchangeRate = null;
    if (null != topOrderInverse) {
      exchangeRate = new BigDecimal(topOrderInverse.getPrice());
    }
    return exchangeRate;
  }

  private Order convertToOrder(DealFormData formData) {
    Order.Type type = Order.Type.BUY;
    if (TradingActionCodeType.SellCode.ID.equals(formData.getTradingActionBox().getValue())) {
      type = Order.Type.SELL;
    }
    Order order = new Order(type, formData.getQuantity().getValue().intValue(), formData.getExchangeRate().getValue().doubleValue(), formData.getDealId().intValue());
    order.setCurrencyPair(formData.getOrderBookType().getValue());
    order.setOwner(formData.getOrganizationId());

    return order;
  }
}
