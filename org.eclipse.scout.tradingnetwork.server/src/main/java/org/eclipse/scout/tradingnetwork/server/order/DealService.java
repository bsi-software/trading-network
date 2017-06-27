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
package org.eclipse.scout.tradingnetwork.server.order;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.exception.ProcessingStatus;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.LongArrayHolder;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.server.clientnotification.ClientNotificationRegistry;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.eclipse.scout.rt.shared.session.Sessions;
import org.eclipse.scout.tradingnetwork.server.orderbook.OrderBookService;
import org.eclipse.scout.tradingnetwork.server.orderbook.model.Order;
import org.eclipse.scout.tradingnetwork.server.orderbook.model.OrderBookCache;
import org.eclipse.scout.tradingnetwork.server.sql.SQLs;
import org.eclipse.scout.tradingnetwork.shared.notification.OrganizationDealMatchedNotification;
import org.eclipse.scout.tradingnetwork.shared.notification.OrganizationDealPublishedNotification;
import org.eclipse.scout.tradingnetwork.shared.order.CreateEventPermission;
import org.eclipse.scout.tradingnetwork.shared.order.DealFormData;
import org.eclipse.scout.tradingnetwork.shared.order.DealsTablePageData;
import org.eclipse.scout.tradingnetwork.shared.order.IDealService;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType;
import org.eclipse.scout.tradingnetwork.shared.order.OwnDealsTablePageData;
import org.eclipse.scout.tradingnetwork.shared.order.ReadEventPermission;
import org.eclipse.scout.tradingnetwork.shared.order.StatusCodeType;
import org.eclipse.scout.tradingnetwork.shared.order.TradingActionCodeType;
import org.eclipse.scout.tradingnetwork.shared.order.UpdateDealPermission;
import org.eclipse.scout.tradingnetwork.shared.organization.IOrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      if (StringUtility.hasText(transactionHash)) {
        formData.setStatus(StatusCodeType.PendingCode.ID);
        formData.setPublishTransactionHash(transactionHash);
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
  public DealFormData loadByDealNr(String orderBookId, Long dealNr) {
    if (!ACCESS.check(new ReadEventPermission())) {
      throw new VetoException(TEXTS.get("InsufficientPrivileges"));
    }

    DealFormData formData = new DealFormData();
    formData.setDealNr(dealNr.toString());
    formData.getOrderBookType().setValue(orderBookId);
    SQL.selectInto(SQLs.DEAL_SELECT_BY_DEAL_NR, formData);

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
  public Double getCurrentExchangeRate(String orderBookId, String tradingActionId) throws ProcessingException {
    Order topOrderInverse = null;
    OrderBookService service = BEANS.get(OrderBookService.class);
    if (TradingActionCodeType.BuyCode.ID.equals(tradingActionId)) {
      topOrderInverse = service.getTopSellOrder(orderBookId);
    }
    else if (TradingActionCodeType.SellCode.ID.equals(tradingActionId)) {
      topOrderInverse = service.getTopBuyOrder(orderBookId);
    }
    Double exchangeRate = null;
    if (null != topOrderInverse) {
      exchangeRate = topOrderInverse.getPrice();
    }
    return exchangeRate;
  }

  public void checkStatusForPendingOrders() {
    for (ICode<String> code : BEANS.get(OrderBookTypeCodeType.class).getCodes()) {
      checkStatusForPendingOrders(code.getId(), new ArrayList<Order>());
    }
  }

  public void checkStatusForPendingOrders(String orderBookTypeId, List<Order> orders) throws ProcessingException {
    if (StringUtility.isNullOrEmpty(orderBookTypeId)) {
      throw new ProcessingException(new ProcessingStatus("orderBookId is not allowed to be null or empty."));
    }

    if (orders == null || orders.size() == 0) {
      orders = BEANS.get(OrderBookCache.class).loadOrders(orderBookTypeId);
    }

    LongArrayHolder pendingDeals = new LongArrayHolder();
    LongArrayHolder pendingDealNrs = new LongArrayHolder();
    SQL.selectInto(SQLs.DEALS_SELECT_IN_STATUS_FORM_ORDERBOOK,
        new NVPair("orderBookType", orderBookTypeId),
        new NVPair("status", StatusCodeType.PendingCode.ID),
        new NVPair("dealId", pendingDeals),
        new NVPair("dealNr", pendingDealNrs));

    for (Long dealId : pendingDeals.getValue()) {
      for (Order order : orders) {
        if (CompareUtility.equals(dealId.intValue(), order.getExtId())) {
          Long dealNr = TypeCastUtility.castValue(order.getId(), Long.class);
          updateDealStatusAndSaveDealNr(dealId, dealNr, StatusCodeType.PublishedCode.ID);
          DealFormData formData = new DealFormData();
          formData.setDealId(dealId);
          formData = load(formData);
          String userId = BEANS.get(IOrganizationService.class).getUserIdForOrganization(formData.getOrganizationId());
          if (StringUtility.hasText(userId)) {
            BEANS.get(ClientNotificationRegistry.class).putForUser(userId, new OrganizationDealPublishedNotification(formData));
          }
        }
      }
    }
  }

  public void checkForExecutedOrders() {
    for (ICode<String> code : BEANS.get(OrderBookTypeCodeType.class).getCodes()) {
      checkForExecutedOrders(code.getId(), new ArrayList<Order>());
    }
  }

  public void checkForExecutedOrders(String orderBookTypeId, List<Order> orders) throws ProcessingException {
    if (StringUtility.isNullOrEmpty(orderBookTypeId)) {
      throw new ProcessingException(new ProcessingStatus("orderBookId is not allowed to be null or empty."));
    }

    if (orders == null || orders.size() == 0) {
      orders = BEANS.get(OrderBookCache.class).loadExecutedOrders(orderBookTypeId);
    }

    LongArrayHolder publishedDealIdsHolder = new LongArrayHolder();
    LongArrayHolder publishedDealNrsHolder = new LongArrayHolder();
    SQL.selectInto(SQLs.DEALS_SELECT_IN_STATUS_FORM_ORDERBOOK,
        new NVPair("orderBookType", orderBookTypeId),
        new NVPair("status", StatusCodeType.PublishedCode.ID),
        new NVPair("dealId", publishedDealIdsHolder),
        new NVPair("dealNr", publishedDealNrsHolder));

    LongArrayHolder partiallyCompletedDealIdsHolder = new LongArrayHolder();
    LongArrayHolder partiallyCompletedDealNrsHolder = new LongArrayHolder();
    SQL.selectInto(SQLs.DEALS_SELECT_IN_STATUS_FORM_ORDERBOOK,
        new NVPair("orderBookType", orderBookTypeId),
        new NVPair("status", StatusCodeType.PartiallyCompletedCode.ID),
        new NVPair("dealId", partiallyCompletedDealIdsHolder),
        new NVPair("dealNr", partiallyCompletedDealNrsHolder));

    List<Long> dealIds = CollectionUtility.arrayList(publishedDealIdsHolder.getValue());
    dealIds.addAll(CollectionUtility.arrayList(partiallyCompletedDealIdsHolder.getValue()));
    List<Long> dealNrs = CollectionUtility.arrayList(publishedDealNrsHolder.getValue());
    dealIds.addAll(CollectionUtility.arrayList(partiallyCompletedDealNrsHolder.getValue()));
    if (null != dealNrs) {
      int numberOfDeals = dealNrs.size();
      for (int i = 0; i < numberOfDeals; i++) {
        for (Order order : orders) {
          Long orderId = TypeCastUtility.castValue(order.getId(), Long.class);
          if (CompareUtility.equals(orderId, dealNrs.get(i))) {
            DealFormData formData = new DealFormData();
            formData.setDealId(dealIds.get(i));
            formData = load(formData);

            Long dealQuantity = formData.getQuantity().getValue();
            String newStatus = StatusCodeType.CompletedCode.ID;
            if (dealQuantity > TypeCastUtility.castValue(order.getAmount(), Long.class)) {
              newStatus = StatusCodeType.PartiallyCompletedCode.ID;
            }
            formData.setStatus(newStatus);

            formData = store(formData);

            String userId = BEANS.get(IOrganizationService.class).getUserIdForOrganization(formData.getOrganizationId());
            if (StringUtility.hasText(userId)) {
              //TODO: [uko] activate
//              updateBankAccountForExecutedOrder(formData, order);
              BEANS.get(ClientNotificationRegistry.class).putForUser(userId, new OrganizationDealMatchedNotification(formData));
            }
          }
        }
      }
    }
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

  private boolean updateDealStatusAndSaveDealNr(Long dealId, Long dealNr, String newStatusCodeId) {
    ICode<String> statusCode = BEANS.get(StatusCodeType.class).getCode(newStatusCodeId);
    if (null == statusCode) {
      throw new ProcessingException("This status: " + newStatusCodeId + " is not a valid status code id. See valid code ids in StatusCodeType.");
    }

    int updateResult = SQL.update(SQLs.DEALS_UPDATE_STATUS_AND_DEAL_NR,
        new NVPair("status", newStatusCodeId),
        new NVPair("dealNr", dealNr),
        new NVPair("dealId", dealId));

    boolean successfull = false;
    if (updateResult > 0) {
      successfull = true;
    }

    return successfull;
  }

  // TODO cleanup
//  private void updateBankAccountForExecutedOrder(DealFormData formData, Order order) {
//    String currencyIdA = StringUtility.substring(order.getCurrencyPair(), 0, 4);
//    String currencyIdB = StringUtility.substring(order.getCurrencyPair(), 3);
//    String tradingActionA = TradingActionCodeType.BuyCode.ID;
//    String tradingActionB = TradingActionCodeType.SellCode.ID;
//    if (!order.isBuy()) {
//      tradingActionA = TradingActionCodeType.SellCode.ID;
//      tradingActionB = TradingActionCodeType.BuyCode.ID;
//    }
//    Double amountA = (double) order.getAmount();
//    Double amountB = order.getPrice() * amountA;
//    BEANS.get(IOrganizationBankAccountService.class).updateBankAccountBalance(formData.getOrganizationId(), currencyIdA, tradingActionA, amountA);
//    BEANS.get(IOrganizationBankAccountService.class).updateBankAccountBalance(formData.getOrganizationId(), currencyIdB, tradingActionB, amountB);
//  }

}
