package com.alvarium.sign;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class KeyInfoTest {
  @Test
  public void toJsonShouldReturnAppropriateRepresentation() {
    final KeyInfo keyInfo = new KeyInfo("./key.json", SignType.Ed25519);
    System.out.println(keyInfo.toJson());
  }  

  @Test
  public void fromJsonShouldReturnAnObjectWithTheRightProps() throws IOException {
    String path = "./src/test/java/com/alvarium/sign/keyInfoData.json";
    final String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);

    final KeyInfo keyInfo = KeyInfo.fromJson(testJson);
    assertEquals("./key.json", keyInfo.getPath());
    assertEquals(SignType.Ed25519, keyInfo.getType());
  }
}
