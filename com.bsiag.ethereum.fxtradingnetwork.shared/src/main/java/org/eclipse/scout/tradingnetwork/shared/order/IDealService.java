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
package org.eclipse.scout.tradingnetwork.shared.order;

import java.math.BigDecimal;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.tradingnetwork.shared.order.DealFormData;
import org.eclipse.scout.tradingnetwork.shared.order.DealsTablePageData;
import org.eclipse.scout.tradingnetwork.shared.order.OwnDealsTablePageData;

@ApplicationScoped
@TunnelToServer
public interface IDealService {

  public enum NotificationEnum {
    Deals
  }

  DealsTablePageData getTableData(SearchFilter filter, String organizationId);

  OwnDealsTablePageData getOwnTableData(SearchFilter filter);

  DealFormData create(DealFormData formData);

  DealFormData load(DealFormData formData);

  DealFormData loadByDealNr(String orderBookId, Long dealNr);

  DealFormData prepareCreate(DealFormData formData);

  DealFormData store(DealFormData formData);

  boolean publish(Long dealId) throws ProcessingException;

  BigDecimal getCurrentExchangeRate(String orderBookId, String tradingActionId) throws ProcessingException;
}
