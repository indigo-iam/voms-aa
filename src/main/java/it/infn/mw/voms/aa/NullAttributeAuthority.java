package it.infn.mw.voms.aa;

public class NullAttributeAuthority implements AttributeAuthority {

  @Override
  public boolean getAttributes(VOMSRequestContext context) {
    return false;
  }

}
