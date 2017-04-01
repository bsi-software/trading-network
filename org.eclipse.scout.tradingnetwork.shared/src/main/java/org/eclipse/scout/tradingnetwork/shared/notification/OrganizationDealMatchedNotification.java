package org.eclipse.scout.tradingnetwork.shared.notification;

import java.io.Serializable;

import org.eclipse.scout.tradingnetwork.shared.order.DealFormData;

public class OrganizationDealMatchedNotification implements Serializable {

  private static final long serialVersionUID = 1L;

  private DealFormData m_matchedDeal;

  public OrganizationDealMatchedNotification(DealFormData matchedDeal) {
    m_matchedDeal = matchedDeal;
  }

  public DealFormData getMatchedDeal() {
    return m_matchedDeal;
  }

}
