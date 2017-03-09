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
package com.bsiag.ethereum.fxtradingnetwork.events.client;

import com.bsiag.ethereum.fxtradingnetwork.events.account.AccountPageExtension;
import com.bsiag.ethereum.fxtradingnetwork.events.client.event.EventOutlineExtension;
import com.bsiag.ethereum.fxtradingnetwork.events.client.event.EventPageExtension;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonFormTabExtension;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonFormWalletTabExtension;
import com.bsiag.ethereum.fxtradingnetwork.events.client.person.PersonTablePageExtension;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.person.PersonFormTabExtensionData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.person.PersonFormWalletTabExtensionData;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.person.PersonTablePageDataExtension;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.IPlatform.State;
import org.eclipse.scout.rt.platform.IPlatformListener;
import org.eclipse.scout.rt.platform.PlatformEvent;
import org.eclipse.scout.rt.shared.extension.IExtensionRegistry;

public class PlatformListener implements IPlatformListener {

  @Override
  public void stateChanged(PlatformEvent event) {
    if (event.getState() == State.BeanManagerValid) {
      registerExtensions();
    }
  }

  private void registerExtensions() {
    IExtensionRegistry extensionRegistry = BEANS.get(IExtensionRegistry.class);

    // Register UI extensions
    extensionRegistry.register(DesktopQuickAccessMenuExtension.class);
    extensionRegistry.register(PersonFormTabExtension.class);
    extensionRegistry.register(PersonTablePageExtension.class);
    extensionRegistry.register(EventOutlineExtension.class);
    extensionRegistry.register(EventPageExtension.class);

    extensionRegistry.register(PersonFormWalletTabExtension.class);
    extensionRegistry.register(AccountPageExtension.class);

    // Register DTO extensions
    extensionRegistry.register(PersonFormTabExtensionData.class);
    extensionRegistry.register(PersonTablePageDataExtension.class);

    extensionRegistry.register(PersonFormWalletTabExtensionData.class);
  }
}
