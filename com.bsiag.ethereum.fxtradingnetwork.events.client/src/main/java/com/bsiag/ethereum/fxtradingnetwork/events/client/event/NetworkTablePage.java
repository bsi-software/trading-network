package com.bsiag.ethereum.fxtradingnetwork.events.client.event;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDateTimeColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractNumberColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import com.bsiag.ethereum.fxtradingnetwork.events.client.event.NetworkTablePage.Table;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.INetworkService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.NetworkTablePageData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.event.TradingActionCodeType;
import com.bsiag.ethereum.fxtradingnetwork.shared.organization.OrganizationLookupCall;

@Data(NetworkTablePageData.class)
public class NetworkTablePage extends AbstractPageWithTable<Table> {

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
  protected String getConfiguredTitle() {
    return TEXTS.get("Network");
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(INetworkService.class).getNetworkTableData(filter));
  }

  @Override
  protected boolean getConfiguredLeaf() {
    return true;
  }

  public class Table extends AbstractTable {

    @Override
    protected boolean getConfiguredSortEnabled() {
      return false;
    }

    @Override
    protected boolean getConfiguredAutoResizeColumns() {
      return true;
    }

    public LocalDealIdColumn getLocalDealIdColumn() {
      return getColumnSet().getColumnByClass(LocalDealIdColumn.class);
    }

    public DealIdColumn getDealIdColumn() {
      return getColumnSet().getColumnByClass(DealIdColumn.class);
    }

    public SortColumn getSortColumn() {
      return getColumnSet().getColumnByClass(SortColumn.class);
    }

    public OwnDealColumn getOwnDealColumn() {
      return getColumnSet().getColumnByClass(OwnDealColumn.class);
    }

    public IsMatchedColumn getIsMatchedColumn() {
      return getColumnSet().getColumnByClass(IsMatchedColumn.class);
    }

    public SideColumn getSideColumn() {
      return getColumnSet().getColumnByClass(SideColumn.class);
    }

    public BuyerSideColumn getBuyerSideColumn() {
      return getColumnSet().getColumnByClass(BuyerSideColumn.class);
    }

    public BuyerOrganizationColumn getBuyerOrganizationColumn() {
      return getColumnSet().getColumnByClass(BuyerOrganizationColumn.class);
    }

    public BuyerTimeColumn getBuyerTimeColumn() {
      return getColumnSet().getColumnByClass(BuyerTimeColumn.class);
    }

    public BuyerAmountColumn getBuyerAmountColumn() {
      return getColumnSet().getColumnByClass(BuyerAmountColumn.class);
    }

    public ExchangeRateColumn getExchangeRateColumn() {
      return getColumnSet().getColumnByClass(ExchangeRateColumn.class);
    }

    public SellerAmountColumn getSellerAmountColumn() {
      return getColumnSet().getColumnByClass(SellerAmountColumn.class);
    }

    public SellerTimeColumn getSellerTimeColumn() {
      return getColumnSet().getColumnByClass(SellerTimeColumn.class);
    }

    public SellerOrganizationColumn getSellerOrganizationColumn() {
      return getColumnSet().getColumnByClass(SellerOrganizationColumn.class);
    }

    public SellerSideColumn getSellerSideColumn() {
      return getColumnSet().getColumnByClass(SellerSideColumn.class);
    }

    @Order(1000)
    public class LocalDealIdColumn extends AbstractStringColumn {
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected boolean getConfiguredDisplayable() {
        return false;
      }
    }

    @Order(2000)
    public class DealIdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Deal-Nr");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(2500)
    public class SortColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Sortierung");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    @Order(3000)
    public class OwnDealColumn extends AbstractBooleanColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("OwnDeal");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected boolean getConfiguredDisplayable() {
        return false;
      }
    }

    @Order(3500)
    public class IsMatchedColumn extends AbstractBooleanColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Match");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected boolean getConfiguredDisplayable() {
        return false;
      }
    }

    @Order(4000)
    public class SideColumn extends AbstractSmartColumn<String> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Action");
      }

      @Override
      protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
        return TradingActionCodeType.class;
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected boolean getConfiguredDisplayable() {
        return false;
      }
    }

    @Order(5000)
    public class BuyerSideColumn extends AbstractSmartColumn<String> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Action");
      }

      @Override
      protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
        return TradingActionCodeType.class;
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(6000)
    public class BuyerOrganizationColumn extends AbstractSmartColumn<String> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Organization");
      }

      @Override
      protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
        return OrganizationLookupCall.class;
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(7000)
    public class BuyerTimeColumn extends AbstractDateTimeColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Time");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(8000)
    public class BuyerAmountColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Amount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(9000)
    public class ExchangeRateColumn extends AbstractNumberColumn<Double> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("ExchangeRate");
      }

      @Override
      protected int getConfiguredWidth() {
        return 150;
      }

      @Override
      protected Double getConfiguredMinValue() {
        return Double.MIN_VALUE;
      }

      @Override
      protected Double getConfiguredMaxValue() {
        return Double.MAX_VALUE;
      }
    }

    @Order(10000)
    public class SellerAmountColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Amount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(11000)
    public class SellerTimeColumn extends AbstractDateTimeColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Time");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(12000)
    public class SellerOrganizationColumn extends AbstractSmartColumn<String> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Organization");
      }

      @Override
      protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
        return OrganizationLookupCall.class;
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(13000)
    public class SellerSideColumn extends AbstractSmartColumn<String> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Action");
      }

      @Override
      protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
        return TradingActionCodeType.class;
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(1000)
    public class ExecuteMergeMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("ExecuteMerge");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected String getConfiguredIconId() {
        return "font:awesomeIcons \f0ec";
      }

      @Override
      protected void execInitAction() {
        List<ITableRow> matchedRows = getIsMatchedColumn().findRows(true);
        if (matchedRows.size() == 2) {
          for (ITableRow row : matchedRows) {
            if (CompareUtility.equals(TradingActionCodeType.SellCode.ID, getSideColumn().getValue(row))
                && getOwnDealColumn().getValue(row)) {
              setVisible(true);
              break;
            }
          }
        }
      }

      @Override
      protected void execAction() {
        List<ITableRow> matchedRows = getIsMatchedColumn().findRows(true);
        if (matchedRows.size() == 2) {
          INetworkService service = BEANS.get(INetworkService.class);
          service.executeMerge(getDealIdColumn().getValue(matchedRows.get(0)), getDealIdColumn().getValue(matchedRows.get(1)));
        }
      }
    }

  }

}
