package com.bsiag.ethereum.fxtradingnetwork.events.client.organization;

import org.eclipse.scout.rt.client.testenvironment.TestEnvironmentClientSession;
import org.eclipse.scout.rt.testing.client.runner.ClientTestRunner;
import org.eclipse.scout.rt.testing.client.runner.RunWithClientSession;
import org.eclipse.scout.rt.testing.platform.mock.BeanMock;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.bsiag.ethereum.fxtradingnetwork.events.shared.organization.IOrganizationBankAccountService;
import com.bsiag.ethereum.fxtradingnetwork.events.shared.organization.OrganizationBankAccountFormData;

@RunWithSubject("anonymous")
@RunWith(ClientTestRunner.class)
@RunWithClientSession(TestEnvironmentClientSession.class)
public class OrganizationBankAccountFormTest {

  @BeanMock
  private IOrganizationBankAccountService m_mockSvc;

  @Before
  public void setup() {
    OrganizationBankAccountFormData answer = new OrganizationBankAccountFormData();
    Mockito.when(m_mockSvc.prepareCreate(Matchers.any(OrganizationBankAccountFormData.class))).thenReturn(answer);
    Mockito.when(m_mockSvc.create(Matchers.any(OrganizationBankAccountFormData.class))).thenReturn(answer);
    Mockito.when(m_mockSvc.load(Matchers.any(OrganizationBankAccountFormData.class))).thenReturn(answer);
    Mockito.when(m_mockSvc.store(Matchers.any(OrganizationBankAccountFormData.class))).thenReturn(answer);
  }

  // TODO [uko] add test cases
}
