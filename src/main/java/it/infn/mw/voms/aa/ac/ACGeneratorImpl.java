/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.voms.aa.ac;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.italiangrid.voms.asn1.VOMSACGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import eu.emi.security.authn.x509.impl.PEMCredential;
import it.infn.mw.voms.aa.VOMSRequestContext;

public enum ACGeneratorImpl implements ACGenerator {

  INSTANCE;

  public static final Logger logger = LoggerFactory.getLogger(ACGeneratorImpl.class);

  private volatile boolean configured = false;

  private VOMSACGenerator acGenerator;

  public synchronized void configure(PEMCredential aaCredential) {

    if (!configured) {
      acGenerator = new VOMSACGenerator(aaCredential);
      configured = true;
    }

  }

  private BigInteger computeSerialNumber() {

    ByteBuffer buf = ByteBuffer.allocate(16);

    UUID r = UUID.randomUUID();

    buf.putLong(r.getMostSignificantBits());
    buf.putLong(r.getLeastSignificantBits());

    buf.flip();

    BigInteger bi = new BigInteger(buf.array());
    return bi.abs();

  }

  @Override
  public byte[] generateVOMSAC(VOMSRequestContext context) throws IOException {

    if (!configured) {
      throw new IllegalStateException("AC generator is not configured!");
    }

    BigInteger serialNo = computeSerialNumber();

    List<String> issuedFqans = Lists.newArrayList(context.getResponse().getIssuedFQANs());

    X509AttributeCertificateHolder ac = acGenerator.generateVOMSAttributeCertificate(issuedFqans,
        context.getResponse().getIssuedGAs(), context.getResponse().getTargets(),
        context.getRequest().getHolderCert(), serialNo, context.getResponse().getNotBefore(),
        context.getResponse().getNotAfter(), context.getVOName(), context.getHost(),
        context.getPort());

    return ac.getEncoded();
  }
}
