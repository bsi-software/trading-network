package com.bsiag.ethereum.fxtradingnetwork.shared.notification;

import java.io.Serializable;

import com.bsiag.ethereum.fxtradingnetwork.shared.order.DealFormData;

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
