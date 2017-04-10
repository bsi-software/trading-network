package org.eclipse.scout.tradingnetwork.shared.notification;

import java.io.Serializable;

import org.eclipse.scout.tradingnetwork.shared.order.DealFormData;

public class OrganizationDealPublishedNotification implements Serializable {

  private static final long serialVersionUID = 1L;

  private DealFormData m_publishedDeal;

  public OrganizationDealPublishedNotification(DealFormData publishedDeal) {
    m_publishedDeal = publishedDeal;
  }

  public DealFormData getPublishedDeal() {
    return m_publishedDeal;
  }

}
