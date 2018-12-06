package it.infn.mw.voms.aa.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamLabel;
import it.infn.mw.voms.aa.VOMSErrorMessage;
import it.infn.mw.voms.aa.VOMSRequestContext;
import it.infn.mw.voms.aa.VOMSResponse.Outcome;
import it.infn.mw.voms.api.VOMSFqan;

public class IamVOMSAttributeResolver implements AttributeResolver {
  public static final Logger LOG = LoggerFactory.getLogger(IamVOMSAttributeResolver.class);

  public static final IamLabel VOMS_ROLE_LABEL = IamLabel.builder().name("voms.role").build();

  protected boolean iamGroupIsVomsGroup(VOMSRequestContext context, IamGroup g) {
    final String voName = context.getVOName();
    final boolean nameMatches = g.getName().equals(voName) || g.getName().startsWith(voName + "/");

    return nameMatches && !g.getLabels().contains(VOMS_ROLE_LABEL);
  }

  protected void noSuchUserError(VOMSRequestContext context) {
    VOMSErrorMessage m = VOMSErrorMessage.noSuchUser(context.getRequest().getHolderSubject(),
        context.getRequest().getHolderIssuer());

    context.getResponse().setOutcome(Outcome.FAILURE);
    context.getResponse().getErrorMessages().add(m);
    context.setHandled(true);
  }

  protected void noSuchAttributeError(VOMSRequestContext context, VOMSFqan fqan) {
    VOMSErrorMessage m = VOMSErrorMessage.noSuchAttribute(fqan.getFqan());

    context.getResponse().setOutcome(Outcome.FAILURE);
    context.getResponse().getErrorMessages().add(m);
    context.setHandled(true);
  }


  protected boolean iamGroupIsVomsRole(IamGroup g) {
    return g.getLabels().contains(VOMS_ROLE_LABEL);
  }

  protected boolean groupMatchesFqan(IamGroup g, VOMSFqan fqan) {
    final String name = fqan.asIamGroupName();
    final boolean nameMatches = name.equals(g.getName());
    if (fqan.isRoleFqan()) {
      return nameMatches && g.getLabels().contains(VOMS_ROLE_LABEL);
    } else {
      return nameMatches;
    }
  }

  protected void issueRequestedFqan(VOMSRequestContext context, VOMSFqan fqan) {
    if (context.getIamAccount().getGroups().stream().anyMatch(g -> groupMatchesFqan(g, fqan))) {
      LOG.debug("Issuing fqan: {}", fqan.getFqan());
      context.getResponse().getIssuedFQANs().add(fqan.getFqan());
    } else {
      noSuchAttributeError(context, fqan);
    }
  }


  protected void issueCompulsoryGroupFqan(VOMSRequestContext context, IamGroup g) {
    final String fqan = "/" + g.getName();
    if (context.getResponse().getIssuedFQANs().add(fqan)) {
      LOG.debug("Issued compulsory fqan: {}", fqan);
    }
  }

  protected boolean requestAccountIsMemberOfGroup(VOMSRequestContext context, String groupName) {
    IamAccount account = context.getIamAccount();
    return account.getGroups().stream().anyMatch(g -> g.getName().equals(groupName));
  }

  protected void resolveRequestedFQANs(VOMSRequestContext requestContext) {
    requestContext.getRequest()
      .getRequestedFQANs()
      .forEach(f -> issueRequestedFqan(requestContext, f));
  }

  protected void resolveCompulsoryFQANs(VOMSRequestContext requestContext) {

    requestContext.getIamAccount()
      .getGroups()
      .stream()
      .filter(g -> iamGroupIsVomsGroup(requestContext, g))
      .forEach(g -> issueCompulsoryGroupFqan(requestContext, g));

    if (requestContext.getResponse().getIssuedFQANs().isEmpty()) {
      noSuchUserError(requestContext);
    }
  }



  @Override
  public void resolveFQANs(VOMSRequestContext requestContext) {

    requestContext.getRequest()
      .getRequestedFQANs()
      .forEach(f -> issueRequestedFqan(requestContext, f));

    resolveCompulsoryFQANs(requestContext);

  }

  @Override
  public void resolveGAs(VOMSRequestContext requestContext) {

  }

}
