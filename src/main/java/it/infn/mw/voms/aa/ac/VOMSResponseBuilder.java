
package it.infn.mw.voms.aa.ac;

import java.util.List;

import it.infn.mw.voms.aa.VOMSErrorMessage;
import it.infn.mw.voms.aa.VOMSWarningMessage;

public interface VOMSResponseBuilder {

  public String createResponse(byte[] acBytes, List<VOMSWarningMessage> warnings);

  public String createErrorResponse(VOMSErrorMessage errorMessage);

  public String createLegacyErrorResponse(VOMSErrorMessage errorMessage);

}
