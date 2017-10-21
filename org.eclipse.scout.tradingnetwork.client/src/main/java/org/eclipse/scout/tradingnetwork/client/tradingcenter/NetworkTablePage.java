package org.eclipse.scout.tradingnetwork.client.tradingcenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
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
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.IDecimalField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.data.basic.FontSpec;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.tradingnetwork.client.tradingcenter.NetworkTablePage.Table;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType;
import org.eclipse.scout.tradingnetwork.shared.order.OrderBookTypeCodeType.NotificationEnum;
import org.eclipse.scout.tradingnetwork.shared.order.TradingActionCodeType;
import org.eclipse.scout.tradingnetwork.shared.organization.OrganizationLookupCall;
import org.eclipse.scout.tradingnetwork.shared.tradingcenter.INetworkService;
import org.eclipse.scout.tradingnetwork.shared.tradingcenter.NetworkTablePageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data(NetworkTablePageData.class)
public class NetworkTablePage extends AbstractPageWithTable<Table> {
  private static final Logger LOG = LoggerFactory.getLogger(NetworkTablePage.class);

  public static final String CONTRACT_ABI_FILE = "C:\\Users\\mzi\\Desktop\\oss\\github\\trading-network\\Solidity\\src\\main\\resources\\OrderBook.abi";

  private boolean m_dataChangeListenerActive;
  private String m_organizationId;
  private String m_orderBookId;
  private NotificationEnum m_dataChancedListenerDataType;

  public NetworkTablePage(String organizationId, String orderBookId) {
    super();
    m_organizationId = organizationId;
    m_orderBookId = orderBookId;
    try {
      m_dataChancedListenerDataType = OrderBookTypeCodeType.NotificationEnum.valueOf(m_orderBookId);
    }
    catch (Exception e) {
      LOG.error("Error on register data change listener for order book: " + m_orderBookId, e);
    }
  }

  @FormData
  public String getOrganizationId() {
    return m_organizationId;
  }

  @FormData
  public void setOrganizationId(String organizationId) {
    this.m_organizationId = organizationId;
  }

  public String getOrderBookId() {
    return m_orderBookId;
  }

  public void setOrderBookId(String orderBookId) {
    m_orderBookId = orderBookId;
  }

  @Override
  protected String getConfiguredTitle() {
    String title = TEXTS.get("Network");
    if (StringUtility.hasText(m_orderBookId)) {
      ICode<String> code = BEANS.get(OrderBookTypeCodeType.class).getCode(m_orderBookId);
      if (null != code) {
        title = "FX Trading " + code.getText();
      }
    }
    return title;
  }

  @Override
  protected void execInitPage() {
    super.execInitPage();
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    NetworkTablePageData pageData = null;
    if (m_dataChangeListenerActive) {
      pageData = BEANS.get(INetworkService.class).getNetworkTableDataFromCache(filter, getOrderBookId());
    }
    else {
      pageData = BEANS.get(INetworkService.class).getNetworkTableData(filter, getOrderBookId());
    }

    importPageData(pageData);
  }

  @Override
  protected void execDataChanged(Object... dataTypes) {
    if (null != m_dataChancedListenerDataType) {
      try {
        m_dataChangeListenerActive = true;
        reloadPage();
      }
      catch (Exception e) {
        LOG.warn("Error on Page reload.", e);
      }
      finally {
        m_dataChangeListenerActive = false;
      }
    }
  }

  @Override
  protected void execPageActivated() {
    if (null != m_dataChancedListenerDataType) {
      registerDataChangeListener(m_dataChancedListenerDataType);
    }
  }

  @Override
  protected void execPageDeactivated() {
    if (null != m_dataChancedListenerDataType) {
      unregisterDataChangeListener(m_dataChancedListenerDataType);
    }
  }

  @Override
  protected boolean getConfiguredLeaf() {
    return true;
  }

  public class Table extends AbstractTable {

    @Order(-1000)
    public class RefreshMenu extends AbstractMenu {

      @Override
      protected String getConfiguredIconId() {
        return "font:awesomeIcons \uf021";
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
      }

      @Override
      protected void execAction() {
        reloadPage();
      }
    }

    @Order(0)
    public class ContractInfoMenu extends AbstractMenu {

      @Override
      protected String getConfiguredIconId() {
        return "font:awesomeIcons \uf05a";
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection, TableMenuType.EmptySpace);
      }

      @Override
      protected void execAction() {
        String contractAbi = readResourceFile(CONTRACT_ABI_FILE);
        String contractAddress = BEANS.get(INetworkService.class).getContractAddress(m_orderBookId);
        String obVariableName = "FxTrading"; //m_orderBookId;

        StringBuffer web3code = new StringBuffer();

        web3code.append("// attach to testrpc container\n");
        web3code.append("docker ps\n");
        web3code.append("docker exec -it <container-id> bash\n\n");
        web3code.append("// inside docker container\n");
        web3code.append("cd /usr/lib/node_modules/ethereumjs-testrpc\rnode\n\n");
        web3code.append("// javascript \n");
        web3code.append("var Web3 = require('web3')\n");
        web3code.append("var web3 = new Web3()\n");
        web3code.append("web3.setProvider(new Web3.providers.HttpProvider('http://localhost:8545'))\n");
        web3code.append("var OrderBook = web3.eth.contract(");
        web3code.append(contractAbi).append(")\n");
        web3code.append("var ").append(obVariableName).append(" = ");
        web3code.append("OrderBook.at('").append(contractAddress).append("')").append("\n");
        web3code.append(obVariableName).append(".symbol()").append("\n");
        web3code.append(obVariableName).append(".getNumberOfBuyOrders()").append("\n");
        web3code.append(obVariableName).append(".getNumberOfSellOrders()").append("\n");
        web3code.append(obVariableName).append(".matchExists()").append("\n");
        web3code.append(obVariableName).append(".topBuyOrderId()").append("\n");
        web3code.append(obVariableName).append(".topSellOrderId()").append("\n");

        ContractWeb3Form infoForm = new ContractWeb3Form();
        infoForm.getWeb3InfoField().setValue(web3code.toString());
        infoForm.start();
      }
    }

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

    public BuyerQuantityColumn getBuyerQuantityColumn() {
      return getColumnSet().getColumnByClass(BuyerQuantityColumn.class);
    }

    public ExchangeRateColumn getExchangeRateColumn() {
      return getColumnSet().getColumnByClass(ExchangeRateColumn.class);
    }

    public SellerQuantityColumn getSellerQuantityColumn() {
      return getColumnSet().getColumnByClass(SellerQuantityColumn.class);
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

    public ExecuteMergeMenu getExecuteMergeMenu() {
      return getMenuByClass(ExecuteMergeMenu.class);
    }

    @Override
    protected void execContentChanged() {
      decorateCellsForMatches();
      selectMatches();
      getExecuteMergeMenu().adjustVisability();
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

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    @Order(8000)
    public class BuyerQuantityColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Quantity");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(9000)
    public class ExchangeRateColumn extends AbstractDecimalColumn<Double> {
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

      @Override
      protected RoundingMode getConfiguredRoundingMode() {
        return RoundingMode.FLOOR;
      }

      @Override
      protected int getConfiguredMaxIntegerDigits() {
        return super.getConfiguredMaxIntegerDigits();
      }

      @Override
      protected IDecimalField<Double> createDefaultEditor() {
        return null;
      }
    }

    @Order(10000)
    public class SellerQuantityColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Quantity");
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

      @Override
      protected boolean getConfiguredVisible() {
        return false;
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
        return CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.MultiSelection, TableMenuType.SingleSelection);
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }

      @Override
      protected String getConfiguredIconId() {
        return "font:awesomeIcons \uf0ec";
      }

      @Override
      protected void execAction() {
        List<ITableRow> matchedRows = getIsMatchedColumn().findRows(true);
        if (matchedRows.size() == 2) {
          ITableRow buyRow = null;
          ITableRow sellRow = null;
          for (ITableRow row : matchedRows) {
            String sideId = getSideColumn().getValue(row);
            if (TradingActionCodeType.BuyCode.ID.equals(sideId)) {
              buyRow = row;
            }
            else if (TradingActionCodeType.SellCode.ID.equals(sideId)) {
              sellRow = row;
            }
          }
          if (null != buyRow && null != sellRow && getOwnDealColumn().getValue(buyRow)) {
            INetworkService service = BEANS.get(INetworkService.class);
            service.executeMerge(getOrderBookId(), getDealIdColumn().getValue(buyRow), getDealIdColumn().getValue(sellRow));
            reloadPage();
          }
        }
      }

      public void adjustVisability() {
        List<ITableRow> matchedRows = getIsMatchedColumn().findRows(true);
        boolean visible = false;
        if (matchedRows.size() == 2) {
          for (ITableRow row : matchedRows) {
            if (CompareUtility.equals(TradingActionCodeType.BuyCode.ID, getSideColumn().getValue(row))
                && getOwnDealColumn().getValue(row)) {
              visible = true;
              break;
            }
          }
        }
        setVisible(visible);
      }
    }

    protected void decorateCellsForMatches() {
      // Highlight matched rows
      List<ITableRow> matchedRows = getIsMatchedColumn().findRows(true);
      for (ITableRow matchedRow : matchedRows) {
        matchedRow.setForegroundColor("AD1B02");
        matchedRow.setBackgroundColor("FCF2D3");
      }
      // Highlight rows with own deals
      List<ITableRow> ownRows = getOwnDealColumn().findRows(true);
      for (ITableRow ownRow : ownRows) {
        ownRow.setFont(FontSpec.parse("BOLD"));
      }
    }

    protected void selectMatches() {
      List<ITableRow> matchedRows = getIsMatchedColumn().findRows(true);
      if (matchedRows.size() > 0) {
        selectRows(matchedRows);
        scrollToSelection();
      }
    }
  }

  private String readResourceFile(String fileName) {
    try {
      File file = new File(fileName);
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);
      StringBuffer text = new StringBuffer();

      br.lines().forEach(line -> {
        if (text.length() > 0) {
          text.append(" ");
        }

        if (line.contains("//")) {
          line = line.substring(0, line.indexOf("//"));
        }

        text.append(line);
      });

      br.close();

      String sourceCode = text.toString();
      return sourceCode.replaceAll("\\s+", " ");
    }
    catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

}
