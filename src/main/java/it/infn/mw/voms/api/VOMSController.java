package it.infn.mw.voms.api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Splitter;

import it.infn.mw.iam.authn.x509.IamX509AuthenticationCredential;
import it.infn.mw.voms.aa.AttributeAuthority;
import it.infn.mw.voms.aa.RequestContextFactory;
import it.infn.mw.voms.aa.VOMSErrorMessage;
import it.infn.mw.voms.aa.VOMSRequestContext;
import it.infn.mw.voms.aa.ac.ACGenerator;
import it.infn.mw.voms.aa.ac.VOMSResponseBuilder;
import it.infn.mw.voms.properties.VomsProperties;


@RestController
public class VOMSController {

  private final Splitter commaSplitter = Splitter.on(',').omitEmptyStrings().trimResults();


  private final VomsProperties vomsProperties;

  private final AttributeAuthority aa;
  private final ACGenerator acGenerator;
  private final VOMSResponseBuilder responseBuilder;

  @Autowired
  public VOMSController(AttributeAuthority aa, VomsProperties props, ACGenerator acGenerator,
      VOMSResponseBuilder responseBuilder) {
    this.aa = aa;
    this.vomsProperties = props;

    this.acGenerator = acGenerator;
    this.responseBuilder = responseBuilder;
  }

  private List<VOMSFqan> getRequestedFqans(String fqans) {
    if (isNullOrEmpty(fqans)) {
      return emptyList();
    } else {
      return commaSplitter.splitToList(fqans)
        .stream()
        .map(VOMSFqan::fromString)
        .collect(Collectors.toList());
    }
  }

  private long getRequestedLifetime(Long lifetime) {

    if (Objects.isNull(lifetime)) {
      return -1;
    }

    return lifetime;
  }

  private List<String> getRequestedTargets(String targets) {
    if (isNullOrEmpty(targets)) {
      return Collections.emptyList();
    }
    return commaSplitter.splitToList(targets);
  }

  @RequestMapping(value = "/generate-ac", method = RequestMethod.GET,
      produces = "text/xml; charset=utf-8")
  @PreAuthorize("hasRole('USER') and hasRole('X509')")
  public String generateAC(VOMSRequestDTO request, Authentication authentication)
      throws IOException {

    IamX509AuthenticationCredential cred =
        (IamX509AuthenticationCredential) authentication.getCredentials();

    VOMSRequestContext context = RequestContextFactory.newContext();
    context.getRequest().setRequesterSubject(cred.getSubject());
    context.getRequest().setRequesterIssuer(cred.getIssuer());
    context.getRequest().setHolderSubject(cred.getSubject());
    context.getRequest().setHolderIssuer(cred.getIssuer());

    context.setHost(vomsProperties.getAa().getHost());
    context.setPort(vomsProperties.getAa().getPort());

    context.setVOName(vomsProperties.getAa().getVoName());

    // populate request
    context.getRequest().setHolderCert(cred.getCertificateChain()[0]);
    context.getRequest().setRequestedFQANs(getRequestedFqans(request.getFqans()));
    context.getRequest().setRequestedValidity(getRequestedLifetime(request.getLifetime()));
    context.getRequest().setTargets(getRequestedTargets(request.getTargets()));

    // get VOMS attributes
    if (!aa.getAttributes(context)) {
      VOMSErrorMessage em = context.getResponse().getErrorMessages().get(0);
      return responseBuilder.createErrorResponse(em);
    } else {
      byte[] acBytes = acGenerator.generateVOMSAC(context);
      return responseBuilder.createResponse(acBytes, context.getResponse().getWarnings());
    }

  }
}
