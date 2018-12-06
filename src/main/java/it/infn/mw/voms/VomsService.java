package it.infn.mw.voms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan({"it.infn.mw.iam.authn.x509","it.infn.mw.voms", "it.infn.mw.iam.persistence"})
@EnableJpaRepositories("it.infn.mw.iam.persistence")
@EntityScan(basePackages= {"it.infn.mw.iam.persistence", "org.mitre.oauth2.model", "org.mitre.openid.connect.model"})
@SpringBootApplication
public class VomsService {

  public static void main(String[] args) {
    SpringApplication.run(VomsService.class, args);
  }

}
