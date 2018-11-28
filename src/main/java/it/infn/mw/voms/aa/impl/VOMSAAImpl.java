package it.infn.mw.voms.aa.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.infn.mw.voms.aa.VOMSResponse.Outcome.SUCCESS;
import static it.infn.mw.voms.aa.VOMSWarningMessage.shortenedAttributeValidity;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.voms.aa.AttributeAuthority;
import it.infn.mw.voms.aa.VOMSErrorMessage;
import it.infn.mw.voms.aa.VOMSRequest;
import it.infn.mw.voms.aa.VOMSRequestContext;
import it.infn.mw.voms.aa.VOMSResponse.Outcome;

public class VOMSAAImpl implements AttributeAuthority {

  private final IamVOMSAccountResolver accountResolver;
  private final AttributeResolver attributeResolver;

  // FIXME: do not use hardcoded value
  private final long maxAttrValidityInSecs = TimeUnit.HOURS.toSeconds(12);
  
  public VOMSAAImpl(IamVOMSAccountResolver accountResolver, AttributeResolver attributeResolver) {
    this.accountResolver = accountResolver;
    this.attributeResolver = attributeResolver;
  }

  protected void checkMembershipValidity(VOMSRequestContext context) {

    IamAccount account = context.getIamAccount();
    VOMSRequest r = context.getRequest();

    if (!account.isActive()) {
      failResponse(context, VOMSErrorMessage.suspendedUser(r.getHolderSubject(),
        r.getHolderIssuer()));
      context.setHandled(true);
      return;
    }
  }
  
  private void handleRequestedValidity(VOMSRequestContext context) {
  
    long validity = maxAttrValidityInSecs;
    long requestedValidity = context.getRequest().getRequestedValidity();

    if (requestedValidity > 0 && requestedValidity < maxAttrValidityInSecs) {
      validity = requestedValidity;
    }
    
    if (requestedValidity > maxAttrValidityInSecs) {
      context.getResponse().getWarnings().add(
        shortenedAttributeValidity(context.getVOName()));
    }

    // FIXME: use TimeProvider
    Calendar cal = Calendar.getInstance();
    Date startDate = cal.getTime();

    cal.add(Calendar.SECOND, (int) validity);

    Date endDate = cal.getTime();

    context.getResponse().setNotAfter(endDate);
    context.getResponse().setNotBefore(startDate);
  }

  private void requestSanityChecks(VOMSRequest request) {
    
    checkNotNull(request);
    checkNotNull(request.getRequesterSubject());
    checkNotNull(request.getHolderSubject());
    
  }

  private void resolveAccount(VOMSRequestContext context) {
    Optional<IamAccount> account = accountResolver.resolveAccountFromRequest(context);
    
    if (account.isPresent()) {
      context.setIamAccount(account.get());
    } else {
      
      VOMSErrorMessage m = VOMSErrorMessage
          .noSuchUser(context.getRequest().getHolderSubject(), context.getRequest().getHolderIssuer());

        context.getResponse().setOutcome(Outcome.FAILURE);
        context.getResponse().getErrorMessages().add(m);
        context.setHandled(true);
    }
  }


  @Override
  public boolean getAttributes(VOMSRequestContext context) {

    requestSanityChecks(context.getRequest());
    
    if (!context.isHandled()) {
      resolveAccount(context);
    }
    
    if (!context.isHandled()) {
      checkMembershipValidity(context);
    }
    
      
    if (!context.isHandled()) {
      resolveFQANs(context);
    }
    
    if (!context.isHandled()) {
      handleRequestedValidity(context);
    }
    
    context.setHandled(true);

    return context.getResponse().getOutcome() == SUCCESS;
  }


  private void resolveFQANs(VOMSRequestContext context) {
    attributeResolver.resolveFQANs(context);
  }

  protected void failResponse(VOMSRequestContext context, VOMSErrorMessage em) {

    context.getResponse().setOutcome(Outcome.FAILURE);
    context.getResponse().getErrorMessages().add(em);
  }
}
