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
package it.infn.mw.voms.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.emi.security.authn.x509.impl.PEMCredential;
import it.infn.mw.iam.authn.x509.IamX509AuthenticationProvider;
import it.infn.mw.iam.authn.x509.IamX509AuthenticationUserDetailService;
import it.infn.mw.iam.authn.x509.InactiveAccountAuthenticationHander;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.voms.aa.AttributeAuthority;
import it.infn.mw.voms.aa.ac.ACGenerator;
import it.infn.mw.voms.aa.ac.ACGeneratorImpl;
import it.infn.mw.voms.aa.ac.VOMSResponseBuilder;
import it.infn.mw.voms.aa.ac.VOMSResponseBuilderImpl;
import it.infn.mw.voms.aa.impl.AttributeResolver;
import it.infn.mw.voms.aa.impl.DefaultIamVomsAccountResolver;
import it.infn.mw.voms.aa.impl.IamVOMSAccountResolver;
import it.infn.mw.voms.aa.impl.IamVOMSAttributeResolver;
import it.infn.mw.voms.aa.impl.VOMSAAImpl;
import it.infn.mw.voms.properties.VomsProperties;

@Configuration
public class VomsConfig {

  @Bean
  IamX509AuthenticationProvider authProvider(IamX509AuthenticationUserDetailService ds) {
    IamX509AuthenticationProvider provider = new IamX509AuthenticationProvider();
    provider.setPreAuthenticatedUserDetailsService(ds);

    return provider;
  }

  @Bean
  InactiveAccountAuthenticationHander noOpInactiveAccountHandler() {
    return a -> {
    };
  }


  @Bean
  PEMCredential aaCredential(VomsProperties properties)
      throws KeyStoreException, CertificateException, IOException {
    return new PEMCredential(new FileInputStream(properties.getTls().getPrivateKeyPath()),
        new FileInputStream(properties.getTls().getCertificatePath()), (char[]) null);
  }

  @Bean
  ACGenerator acGenerator(PEMCredential aaCredential) {

    ACGenerator generator = ACGeneratorImpl.INSTANCE;
    generator.configure(aaCredential);
    return generator;


  }

  @Bean
  IamVOMSAccountResolver iamAccountResolver(IamAccountRepository accountRepo) {
    return new DefaultIamVomsAccountResolver(accountRepo);
  }

  @Bean
  AttributeResolver iamAttributeResolver() {
    return new IamVOMSAttributeResolver();
  }

  @Bean
  AttributeAuthority aa(IamVOMSAccountResolver accountResolver, AttributeResolver attributeResolver,
      VomsProperties props, Clock clock) {

    return new VOMSAAImpl(accountResolver, attributeResolver, props, clock);
  }

  @Bean
  VOMSResponseBuilder responseBuilder() {
    return VOMSResponseBuilderImpl.INSTANCE;
  }

  @Bean
  Clock clock() {
    return Clock.systemDefaultZone();
  }
}
