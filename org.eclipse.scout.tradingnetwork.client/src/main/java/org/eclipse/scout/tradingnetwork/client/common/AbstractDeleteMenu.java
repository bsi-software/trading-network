package org.eclipse.scout.tradingnetwork.client.common;

import java.util.Set;

import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.tradingnetwork.client.Icons;

public class AbstractDeleteMenu extends AbstractMenu {

  @Override
  protected String getConfiguredText() {
    return TEXTS.get("DeleteMenu");
  }

  @Override
  protected String getConfiguredIconId() {
    return Icons.Trash;
  }

  @Override
  protected String getConfiguredKeyStroke() {
    return "alt-d";
  }

  @Override
  protected Set<? extends IMenuType> getConfiguredMenuTypes() {
    return CollectionUtility.<IMenuType> hashSet(
        TableMenuType.MultiSelection, TableMenuType.SingleSelection);
  }

}
