package com.alvarium;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.alvarium.utils.PropertyBag;
import com.alvarium.annotators.Annotator;
import com.alvarium.annotators.AnnotatorException;
import com.alvarium.annotators.AnnotatorFactory;
import com.alvarium.streams.StreamException;
import com.alvarium.utils.ImmutablePropertyBag;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

class MockSdk implements Sdk {
  public void create(PropertyBag properties, byte[] data) {
  }
  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) {
  }
  public void transit(PropertyBag properties, byte[] data) {
  }
  public void close() {
    System.out.println("Connections closed");
  } 
}
public class SdkTest {
  private final String testJson;

  public SdkTest() throws IOException {
    String path = "./src/test/java/com/alvarium/mock-info.json";
    this.testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
  }

  @Test
  public void instantiateSdkShouldNotThrow() throws AnnotatorException, StreamException {
    final SdkInfo sdkInfo = SdkInfo.fromJson(this.testJson);

    // init annotators
    final Annotator[] annotators = new Annotator[sdkInfo.getAnnotators().length]; 
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();

    for (int i = 0; i < annotators.length; i++) {
      annotators[i] = annotatorFactory.getAnnotator(sdkInfo.getAnnotators()[i], sdkInfo.getHash()
          .getType(), sdkInfo.getSignature());
    }

    // init logger
    final Logger logger = LogManager.getRootLogger();
    Configurator.setRootLevel(Level.DEBUG);

    final Sdk sdk = new DefaultSdk(annotators, sdkInfo, logger);
    sdk.close();
  }

  @Test
  public void createShouldReturnSameData() {
    final Sdk sdk = new MockSdk();
    final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String,Object>());
    byte[] oldData = {0xA, 0x1};
    byte[] newData = {0x1, 0xA};
    sdk.create(properties, oldData);
    sdk.mutate(properties, oldData, newData);
    sdk.transit(properties, oldData);
  }
}
