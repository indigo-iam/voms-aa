package it.infn.mw.iam.authn.x509;

import static eu.emi.security.authn.x509.helpers.CertificateHelpers.toX500Name;

import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.springframework.stereotype.Component;

import eu.emi.security.authn.x509.helpers.JavaAndBCStyle;
import eu.emi.security.authn.x509.proxy.ProxyUtils;

@Component
public class CANLProxyHelper implements ProxyCertificateHelper {

  @Override
  public boolean isProxyCertificate(X509Certificate[] chain) {
    return ProxyUtils.isProxy(chain);
  }

  @Override
  public X509Certificate getEndEntityCertificate(X509Certificate[] chain) {
    return ProxyUtils.getEndUserCertificate(chain);
  }

  @Override
  public String getEndEntityCertificateSubject(X509Certificate[] chain) {
    if (ProxyUtils.isProxy(chain)) {
      return resolveEecSubjectFromPrincipal(chain[0].getSubjectX500Principal());
    }
    return chain[0].getSubjectX500Principal().getName(X500Principal.RFC2253);
  }

  private String resolveEecSubjectFromPrincipal(X500Principal subjectX500Principal) {
    X500Name subjectName = toX500Name(subjectX500Principal);
    RDN[] rdns = subjectName.getRDNs();
    
    int lastProxyCnIndex = -1;
    
    for (int index = rdns.length-1; index >=0; index--) {
    
      RDN rdn = rdns[index];
      
      if (!rdn.getFirst().getType().equals(JavaAndBCStyle.CN)) {
        throw new IllegalArgumentException("Last DN RDN is not a CommonName");
      }
      
      String cnValue = IETFUtils.valueToString(rdn.getFirst().getValue());
      if (cnValue.matches("[0-9]\\+")) {
        lastProxyCnIndex = index;
      } else {
        break;
      }
    }
    
    if (lastProxyCnIndex == -1) {
      return subjectX500Principal.getName(X500Principal.RFC2253);
    }
    
    
    RDN[] eecSubjectRdns = new RDN[lastProxyCnIndex];
    System.arraycopy(rdns, 0, eecSubjectRdns, 0, lastProxyCnIndex-1);
    X500Name eecName = new X500Name(JavaAndBCStyle.INSTANCE, eecSubjectRdns);
    return eecName.toString();
  }

  

}
