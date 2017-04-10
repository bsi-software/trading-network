package org.eclipse.scout.tradingnetwork.client.notification;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.desktop.notification.DesktopNotification;
import org.eclipse.scout.rt.client.ui.desktop.notification.IDesktopNotification;
import org.eclipse.scout.rt.platform.status.IStatus;
import org.eclipse.scout.rt.platform.status.Status;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.eclipse.scout.tradingnetwork.client.ClientSession;
import org.eclipse.scout.tradingnetwork.shared.notification.OrganizationDealPublishedNotification;
import org.eclipse.scout.tradingnetwork.shared.order.DealFormData;

public class OrganizationDealPublishedNotificationHandler implements INotificationHandler<OrganizationDealPublishedNotification> {

  @Override
  public void handleNotification(OrganizationDealPublishedNotification notification) {
    ModelJobs.schedule(new IRunnable() {

      @Override
      public void run() throws Exception {
        DealFormData deal = notification.getPublishedDeal();
        String statusMessage = TEXTS.get("DealWithNumberWasPublished", deal.getOrderBookType().getValue(), deal.getDealNr());
        Status status = new Status(statusMessage, IStatus.INFO);
        DesktopNotification desktopNotification = new DesktopNotification(status, IDesktopNotification.INFINITE_DURATION, true);
        ClientSession.get().getDesktop().addNotification(desktopNotification);
      }
    }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
  }

}
