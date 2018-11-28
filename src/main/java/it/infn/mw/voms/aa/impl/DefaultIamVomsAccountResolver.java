package it.infn.mw.voms.aa.impl;

import java.util.Optional;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.voms.aa.VOMSRequestContext;

public class DefaultIamVomsAccountResolver implements IamVOMSAccountResolver {

  IamAccountRepository accountRepo;
  
  public DefaultIamVomsAccountResolver(IamAccountRepository repo) {
    this.accountRepo = repo;
  }
  
  @Override
  public Optional<IamAccount> resolveAccountFromRequest(VOMSRequestContext requestContext) {
    
    String certificateSubject = requestContext.getRequest().getRequesterSubject();
    
    return accountRepo.findByCertificateSubject(certificateSubject);
    
  }

}
