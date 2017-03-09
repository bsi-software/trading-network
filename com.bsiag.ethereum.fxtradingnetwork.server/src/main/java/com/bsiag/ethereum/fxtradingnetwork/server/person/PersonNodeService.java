package com.bsiag.ethereum.fxtradingnetwork.server.person;

import com.bsiag.ethereum.fxtradingnetwork.shared.person.IPersonNodeService;
import com.bsiag.ethereum.fxtradingnetwork.shared.person.PersonNodeTablePageData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

public class PersonNodeService implements IPersonNodeService {

  @Override
  public PersonNodeTablePageData getPersonNodeTableData(SearchFilter filter) {
    PersonNodeTablePageData pageData = new PersonNodeTablePageData();
    // TODO [mzi] fill pageData.
    return pageData;
  }
}
