package it.infn.mw.voms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import it.infn.mw.iam.authn.x509.IamX509AuthenticationProvider;
import it.infn.mw.iam.authn.x509.IamX509PreauthenticationProcessingFilter;
import it.infn.mw.iam.authn.x509.IamX509PreauthenticationProcessingFilter.AuthenticationMode;
import it.infn.mw.iam.authn.x509.X509AuthenticationCredentialExtractor;
import it.infn.mw.iam.authn.x509.voms.VOMSAccessDeniedHandler;
import it.infn.mw.iam.authn.x509.voms.VOMSAuthenticationEntryPoint;
import it.infn.mw.voms.aa.ac.VOMSResponseBuilder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  public static class VomsConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private X509AuthenticationCredentialExtractor x509CredentialExtractor;

    @Autowired
    private IamX509AuthenticationProvider x509AuthenticationProvider;

    @Autowired
    private VOMSResponseBuilder responseBuilder;
    
    public AuthenticationSuccessHandler successHandler() {
      return (req, res, auth) -> {};
    }

    public IamX509PreauthenticationProcessingFilter iamX509Filter() {
      return new IamX509PreauthenticationProcessingFilter(x509CredentialExtractor,
          x509AuthenticationProvider, successHandler(), AuthenticationMode.IMPLICIT);
    }

    public AuthenticationEntryPoint vomsAuthenticationEntryPoint() {
      return new VOMSAuthenticationEntryPoint(responseBuilder);
    }

    public AccessDeniedHandler vomsAccessDeniedHandler() {
      return new VOMSAccessDeniedHandler(responseBuilder);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.requestMatchers()
        .antMatchers("/generate-ac**")
        .and()
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
            .antMatchers("/generate-ac**").authenticated()
        .and()
          .exceptionHandling()
            .accessDeniedHandler(vomsAccessDeniedHandler())
            .authenticationEntryPoint(vomsAuthenticationEntryPoint())
        .and()
          .csrf().disable()
        .addFilter(iamX509Filter());
      // @formatter:on
    }
  }

}
