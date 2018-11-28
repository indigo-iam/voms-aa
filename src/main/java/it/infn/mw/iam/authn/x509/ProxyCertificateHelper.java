package it.infn.mw.iam.authn.x509;

import java.security.cert.X509Certificate;

public interface ProxyCertificateHelper {
  
  boolean isProxyCertificate(X509Certificate[] chain);
  
  X509Certificate getEndEntityCertificate(X509Certificate[] chain);
  
  String getEndEntityCertificateSubject(X509Certificate[] chain);

}
