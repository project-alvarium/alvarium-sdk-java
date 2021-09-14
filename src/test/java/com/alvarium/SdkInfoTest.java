package com.alvarium;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignType;
import com.alvarium.streams.MqttConfig;

import org.junit.Test;

public class SdkInfoTest {
  private final String testJson;
  private final AnnotationType[] annotators = {AnnotationType.TLS,AnnotationType.MOCK};

  public SdkInfoTest() throws IOException {
    String path = "./src/test/java/com/alvarium/sdk-info.json";
    this.testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
  }

  @Test
  public void fromJsonShouldReturnSdkInfo(){
    SdkInfo sdkInfo = SdkInfo.fromJson(this.testJson);

    assertEquals(true, Arrays.equals(this.annotators, sdkInfo.getAnnotators()));
    assertEquals(HashType.SHA256Hash, sdkInfo.getHash().getType());
    assertEquals(SignType.Ed25519, sdkInfo.getSignature().getPrivateKey().getType());
    assertEquals(MqttConfig.class, sdkInfo.getStream().getConfig().getClass());
  } 
}
