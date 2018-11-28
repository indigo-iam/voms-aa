package it.infn.mw.voms.aa.ac;

import java.io.IOException;

import eu.emi.security.authn.x509.impl.PEMCredential;
import it.infn.mw.voms.aa.VOMSRequestContext;

public class NullACGenerator implements ACGenerator {

  @Override
  public void configure(PEMCredential aaCredential) {

  }

  @Override
  public byte[] generateVOMSAC(VOMSRequestContext context) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

}
