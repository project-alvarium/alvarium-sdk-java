
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
package com.alvarium.sign;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class SignatureInfoTest {
  @Test
  public void toJsonShouldReturnAppropriateRepresentation() {
    final KeyInfo keyInfo = new KeyInfo("./key.json", SignType.Ed25519);
    final SignatureInfo sig = new SignatureInfo(keyInfo, keyInfo);
    
    System.out.println(sig.toJson());
  }

  @Test
  public void fromJsonShouldReturnAnObjectWithTheRightProps() throws IOException{
    String path = "./src/test/java/com/alvarium/sign/signatureInfoData.json";
    final String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);

    final SignatureInfo sig = SignatureInfo.fromJson(testJson);
    assertEquals(KeyInfo.class, sig.getPublicKey().getClass());
    assertEquals(KeyInfo.class, sig.getPrivateKey().getClass());
  }
}
