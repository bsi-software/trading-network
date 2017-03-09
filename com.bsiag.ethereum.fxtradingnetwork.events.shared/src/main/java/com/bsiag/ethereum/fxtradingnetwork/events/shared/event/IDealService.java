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
package com.bsiag.ethereum.fxtradingnetwork.events.shared.event;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@ApplicationScoped
@TunnelToServer
public interface IDealService {

  public enum notificationEnum {
    Deals
  }

  DealsTablePageData getTableData(SearchFilter filter, String organizationId);

  OwnDealsTablePageData getOwnTableData(SearchFilter filter);

  DealFormData create(DealFormData formData);

  DealFormData load(DealFormData formData);

  DealFormData prepareCreate(DealFormData formData);

  DealFormData store(DealFormData formData);
}
