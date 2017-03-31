package com.bsiag.ethereum.fxtradingnetwork.client.notification;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.desktop.notification.DesktopNotification;
import org.eclipse.scout.rt.client.ui.desktop.notification.IDesktopNotification;
import org.eclipse.scout.rt.platform.status.IStatus;
import org.eclipse.scout.rt.platform.status.Status;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;

import com.bsiag.ethereum.fxtradingnetwork.client.ClientSession;
import com.bsiag.ethereum.fxtradingnetwork.shared.notification.OrganizationDealMatchedNotification;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.DealFormData;
import com.bsiag.ethereum.fxtradingnetwork.shared.order.StatusCodeType;

public class OrganizationDealMatchedNotificationHandler implements INotificationHandler<OrganizationDealMatchedNotification> {

  @Override
  public void handleNotification(OrganizationDealMatchedNotification notification) {
    ModelJobs.schedule(new IRunnable() {

      @Override
      public void run() throws Exception {
        DealFormData deal = notification.getMatchedDeal();
        String statusMessage = TEXTS.get("DealWithNumberWasMatched", deal.getOrderBookType().getValue(), deal.getDealNr());
        if (StatusCodeType.PartiallyCompletedCode.ID.equals(deal.getStatus())) {
          statusMessage = TEXTS.get("DealWithNumberWasPartiallyMatched", deal.getOrderBookType().getValue(), deal.getDealNr());
        }
        Status status = new Status(statusMessage, IStatus.OK);
        DesktopNotification desktopNotification = new DesktopNotification(status, IDesktopNotification.INFINITE_DURATION, true);
        ClientSession.get().getDesktop().addNotification(desktopNotification);
      }
    }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
  }

}