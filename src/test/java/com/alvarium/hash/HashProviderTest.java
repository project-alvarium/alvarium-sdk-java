package com.alvarium.hash;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class HashProviderTest {
  @Test
  public void noneProviderShouldReturnTheSameString() throws HashTypeException {
    HashProviderFactory hashProviderFactory = new HashProviderFactory();
    HashProvider sut = hashProviderFactory.getProvider(HashType.NoHash);
    for (int i = 0; i < 10; i++) {
      String randomString = this.generateRandomString(64);
      System.out.println(String.format("test string: %s", randomString));
      String result = sut.derive(randomString.getBytes());
      assertEquals(randomString, result);
    }
  }

  String generateRandomString(int length) {
    int lowerLimit = 97;
    int upperLimit = 122;
    Random random = new Random();
    String generatedString = random.ints(lowerLimit, upperLimit + 1).limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    return generatedString;
  }
}
