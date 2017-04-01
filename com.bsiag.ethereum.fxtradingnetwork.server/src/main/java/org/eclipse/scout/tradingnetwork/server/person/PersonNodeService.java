package org.eclipse.scout.tradingnetwork.server.person;

import org.eclipse.scout.tradingnetwork.shared.person.IPersonNodeService;
import org.eclipse.scout.tradingnetwork.shared.person.PersonNodeTablePageData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

public class PersonNodeService implements IPersonNodeService {

  @Override
  public PersonNodeTablePageData getPersonNodeTableData(SearchFilter filter) {
    PersonNodeTablePageData pageData = new PersonNodeTablePageData();
    // TODO [mzi] fill pageData.
    return pageData;
  }
}
