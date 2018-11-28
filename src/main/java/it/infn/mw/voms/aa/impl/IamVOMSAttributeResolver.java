package it.infn.mw.voms.aa.impl;

import it.infn.mw.voms.aa.VOMSRequestContext;

public class IamVOMSAttributeResolver implements AttributeResolver{

  
  protected void resolveRequestedFQANs(VOMSRequestContext requestContext) {
      // TBD
  }
  
  protected void resolveCompulsoryFQANs(VOMSRequestContext requestContext) {
   
    requestContext.getResponse().getIssuedFQANs().add("/"+requestContext.getVOName());
  }
  
  protected void filterFQANs(VOMSRequestContext requestContext) {
    // TBD
  }
      
  @Override
  public void resolveFQANs(VOMSRequestContext requestContext) {
    
    resolveRequestedFQANs(requestContext);
    resolveCompulsoryFQANs(requestContext);
    filterFQANs(requestContext);
    
  }

  @Override
  public void resolveGAs(VOMSRequestContext requestContext) {
   // tbd 
  }

}
