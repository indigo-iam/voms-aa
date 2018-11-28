package it.infn.mw.voms.api;

import javax.annotation.Generated;

import it.infn.mw.voms.aa.VOMSNamingScheme;

public class VOMSFqan {

  private final String fqan;

  private final boolean roleFqan;

  private VOMSFqan(String fqan) {
    roleFqan = VOMSNamingScheme.isRole(fqan);
    this.fqan = fqan;
  }

  @Generated("eclipse")
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fqan == null) ? 0 : fqan.hashCode());
    return result;
  }

  @Generated("eclipse")
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VOMSFqan other = (VOMSFqan) obj;
    if (fqan == null) {
      if (other.fqan != null)
        return false;
    } else if (!fqan.equals(other.fqan))
      return false;
    return true;
  }

  public String getFqan() {
    return fqan;
  }

  public boolean isRoleFqan() {
    return roleFqan;
  }

  public static VOMSFqan fromString(String fqan) {
    return new VOMSFqan(fqan);
  }
}
