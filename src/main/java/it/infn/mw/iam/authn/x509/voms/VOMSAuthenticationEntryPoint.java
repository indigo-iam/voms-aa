package it.infn.mw.iam.authn.x509.voms;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import it.infn.mw.voms.aa.VOMSErrorMessage;
import it.infn.mw.voms.aa.ac.VOMSResponseBuilder;


public class VOMSAuthenticationEntryPoint implements AuthenticationEntryPoint {


  final VOMSResponseBuilder responseBuilder;

  public VOMSAuthenticationEntryPoint(VOMSResponseBuilder responseBuilder) {
    this.responseBuilder = responseBuilder;
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    
    final VOMSErrorMessage error = VOMSErrorMessage.unauthenticatedClient();
    String vomsResponse = responseBuilder.createErrorResponse(error);
    response.setStatus(error.getError().getHttpStatus());
    response.getOutputStream().write(vomsResponse.getBytes());
  }

}
