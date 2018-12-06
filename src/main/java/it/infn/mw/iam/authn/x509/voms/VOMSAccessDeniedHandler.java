package it.infn.mw.iam.authn.x509.voms;

import static it.infn.mw.voms.aa.VOMSErrorMessage.noSuchUser;
import static it.infn.mw.voms.aa.VOMSErrorMessage.unauthenticatedClient;
import static java.util.Objects.isNull;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import it.infn.mw.iam.authn.x509.IamX509AuthenticationCredential;
import it.infn.mw.voms.aa.VOMSErrorMessage;
import it.infn.mw.voms.aa.ac.VOMSResponseBuilder;

public class VOMSAccessDeniedHandler implements AccessDeniedHandler {

  final VOMSResponseBuilder responseBuilder;

  public VOMSAccessDeniedHandler(VOMSResponseBuilder responseBuilder) {
    this.responseBuilder = responseBuilder;
  }


  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    SecurityContext context = SecurityContextHolder.getContext();

    VOMSErrorMessage error;

    if (isNull(context.getAuthentication()) || !context.getAuthentication().isAuthenticated()) {
      error = unauthenticatedClient();

    } else {
      
      IamX509AuthenticationCredential cred =
          (IamX509AuthenticationCredential) context.getAuthentication().getCredentials();

      error = noSuchUser(cred.getSubject(), cred.getIssuer());
    }

    String vomsResponse = responseBuilder.createErrorResponse(error);
    response.setStatus(error.getError().getHttpStatus());
    response.getOutputStream().write(vomsResponse.getBytes());


  }

}
