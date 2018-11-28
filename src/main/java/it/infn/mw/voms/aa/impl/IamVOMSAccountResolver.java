package it.infn.mw.voms.aa.impl;

import java.util.Optional;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.voms.aa.VOMSRequestContext;

public interface IamVOMSAccountResolver {

  Optional<IamAccount> resolveAccountFromRequest(VOMSRequestContext requestContext);

}
