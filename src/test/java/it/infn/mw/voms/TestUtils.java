package it.infn.mw.voms;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

public class TestUtils {

  public static String loadClasspathResourceContent(String resource)
      throws IOException {

    ClassPathResource cpr = new ClassPathResource(resource);
    InputStreamReader reader =
        new InputStreamReader(cpr.getInputStream(), StandardCharsets.UTF_8);

    return FileCopyUtils.copyToString(reader);
  }
}