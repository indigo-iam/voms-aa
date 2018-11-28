package it.infn.mw.voms.aa;

public class VOMSException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public VOMSException(String message) {
    super(message);
  }

  public VOMSException(Throwable cause) {
    super(cause);
  }

  public VOMSException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
