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
package com.bsiag.ethereum.fxtradingnetwork.server.sql;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.platform.config.AbstractBooleanConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractSubjectConfigProperty;

// tag::structure[]
public class DatabaseProperties {

  public static class DatabaseAutoCreateProperty extends AbstractBooleanConfigProperty {
    // defines default value and key

    @Override
    protected Boolean getDefaultValue() {
      return Boolean.FALSE; // <1>
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.autocreate"; // <2>
    }
  }

  public static class DatabaseAutoPopulateProperty extends AbstractBooleanConfigProperty {
    // defines default value and key
    // end::structure[]

    @Override
    protected Boolean getDefaultValue() {
      return Boolean.FALSE;
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.autopopulate";
    }
    // tag::structure[]
  }

  public static class JdbcMappingNameProperty extends AbstractStringConfigProperty {
    // defines default value and key
    // end::structure[]

    @Override
    protected String getDefaultValue() {
      return "jdbc:postgresql://10.0.42.146:5432/postgres";
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.jdbc.mapping.name";
    }
    // tag::structure[]
  }

  public static class JdbcUsernameProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.jdbc.username";
    }

  }

  public static class JdbcPasswordProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return "fxtradingnetwork.database.jdbc.password";
    }

  }

  public static class SuperUserSubjectProperty extends AbstractSubjectConfigProperty {
    // defines default value and key
    // end::structure[]

    @Override
    protected Subject getDefaultValue() {
      return convertToSubject("system");
    }

    @Override
    public String getKey() {
      return "fxtradingnetwork.superuser";
    }
    // tag::structure[]
  }
}
// end::structure[]
