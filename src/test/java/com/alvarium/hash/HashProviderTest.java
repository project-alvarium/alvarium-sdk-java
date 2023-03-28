// 
/*******************************************************************************
 * Copyright 2021 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package com.alvarium.hash;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class HashProviderTest {
  private final String[] testCases = {"alvarium is the best", "foo bar"};
  private final String[] sha256Hashes = {
      "DD22AA6A8CF771E812349DA134CB266471DEBB549B0E2908091C61B1C47FA853",
      "FBC1A9F858EA9E177916964BD88C3D37B91A1E84412765E29950777F265C4B75"};
  private final String[] md5Hashes = { "8F907CCF83C58F0D8E84BABA9DF0DBB7", 
      "327B6F07435811239BC47E1544353273" };

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

  @Test
  public void sha256ProviderShouldGenerateAppropriateHashes() throws HashTypeException {
    HashProviderFactory hashProviderFactory = new HashProviderFactory();
    HashProvider sut = hashProviderFactory.getProvider(HashType.SHA256Hash);

    for (int i = 0; i < testCases.length; i++) {
      final String resultedHash = sut.derive(testCases[i].getBytes());
      assertEquals(sha256Hashes[i], resultedHash);
    }
  }

  @Test
  public void md5ProviderShouldGenerateAppropriateHashes() throws HashTypeException {
    HashProviderFactory hashProviderFactory = new HashProviderFactory();
    HashProvider sut = hashProviderFactory.getProvider(HashType.MD5Hash);
    
    for (int i = 0; i < testCases.length; i++) {
      final String result = sut.derive(testCases[i].getBytes()); 
      assertEquals(md5Hashes[i], result);
    }
  }

  @Test
  public void md5ProviderUpdateReturnsSameAsDerive() throws Exception {
    HashProviderFactory hashProviderFactory = new HashProviderFactory();
    HashProvider provider = hashProviderFactory.getProvider(HashType.MD5Hash);
    
    final String exampleString = "foo";
    provider.update(exampleString.getBytes());
    String hash1 = provider.getValue();
    String hash2 = provider.derive(exampleString.getBytes());

    assert hash1.equals(hash2);
  }

  @Test
  public void sha256ProviderUpdateReturnsSameAsDerive() throws Exception {
    HashProviderFactory hashProviderFactory = new HashProviderFactory();
    HashProvider provider = hashProviderFactory.getProvider(HashType.SHA256Hash);

    final String exampleString = "foo";
    provider.update(exampleString.getBytes());
    String hash1 = provider.getValue();
    String hash2 = provider.derive(exampleString.getBytes());

    assert hash1.equals(hash2);
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
