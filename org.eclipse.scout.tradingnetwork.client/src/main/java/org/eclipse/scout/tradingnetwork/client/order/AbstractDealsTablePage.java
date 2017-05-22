package org.eclipse.scout.tradingnetwork.client.order;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.tradingnetwork.client.Icons;
import org.eclipse.scout.tradingnetwork.client.common.AbstractEditMenu;
import org.eclipse.scout.tradingnetwork.client.common.AbstractNewMenu;
import org.eclipse.scout.tradingnetwork.shared.order.AbstractDealsTablePageData;
import org.eclipse.scout.tradingnetwork.shared.order.IDealService;
import org.eclipse.scout.tradingnetwork.shared.order.StatusCodeType;

@Data(AbstractDealsTablePageData.class)
public abstract class AbstractDealsTablePage<T extends AbstractDealsTablePage<T>.Table> extends AbstractPageWithTable<AbstractDealsTablePage<T>.Table> {

  private String organizationId;

  @FormData
  public String getOrganizationId() {
    return organizationId;
  }

  @FormData
  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  @Override
  protected String getConfiguredIconId() {
    return Icons.World;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Deals");
  }

  @Override
  protected boolean getConfiguredLeaf() {
    return true;
  }

  @Override
  protected void execInitPage() {
    registerDataChangeListener(IDealService.NotificationEnum.Deals);
    super.execInitPage();
  }

  public class Table extends AbstractDealsTable {

    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() {
      return EditMenu.class;
    }

    @Order(1000)
    public class EditMenu extends AbstractEditMenu {

      @Override
      protected void execOwnerValueChanged(Object newOwnerValue) {
        boolean isVisible = false;
        if (StatusCodeType.InactiveCode.ID.equals(getStatusColumn().getValue(getSelectedRow()))) {
          isVisible = (true);
        }
        setVisible(isVisible);
      }

      @Override
      protected void execAction() {
        final DealForm form = new DealForm();
        form.setDealId(getDealIdColumn().getSelectedValue());
        form.startModify();
      }
    }

    @Order(1500)
    public class PublishDealMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("PublishDeal");
      }

      @Override
      protected String getConfiguredIconId() {
        return "font:awesomeIcons \uf1d8";
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execOwnerValueChanged(Object newOwnerValue) {
        boolean isVisible = false;
        if (StatusCodeType.InactiveCode.ID.equals(getStatusColumn().getValue(getSelectedRow()))) {
          isVisible = (true);
        }
        setVisible(isVisible);
      }

      @Override
      protected void execAction() {
        for (Long dealId : getDealIdColumn().getSelectedValues()) {
          BEANS.get(IDealService.class).publish(dealId);
        }
        reloadPage();
      }
    }

    @Order(2000)
    public class NewMenu extends AbstractNewMenu {

      @Override
      protected void execAction() {
        final DealForm form = new DealForm();
        form.setOrganizationId(organizationId);
        form.addFormListener(new FormListener() {

          @Override
          public void formChanged(FormEvent e) {
            if (FormEvent.TYPE_CLOSED == e.getType() && form.isFormStored()) {
              reloadPage();
            }
          }
        });

        form.startNew();
      }
    }
  }
}
