
/*******************************************************************************
 * Copyright 2023 Dell Inc.
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
package com.alvarium;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alvarium.annotators.MockAnnotatorConfig;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignType;
import com.alvarium.streams.MqttConfig;

import org.junit.Test;

public class SdkInfoTest {
  private final String testJson;

  public SdkInfoTest() throws IOException {
    String path = "./src/test/java/com/alvarium/sdk-info.json";
    this.testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
  }

  @Test
  public void fromJsonShouldReturnSdkInfo(){
    SdkInfo sdkInfo = SdkInfo.fromJson(this.testJson);

    assert sdkInfo.getAnnotators().length == 2;

    assert sdkInfo.getAnnotators()[0].getKind() == AnnotationType.TPM;

    assert sdkInfo.getAnnotators()[1].getKind() == AnnotationType.MOCK;
    assert MockAnnotatorConfig.class.cast(sdkInfo.getAnnotators()[1]).getShouldSatisfy() == false;


    assert sdkInfo.getHash().getType() == HashType.SHA256Hash;
    assert sdkInfo.getSignature().getPrivateKey().getType() == SignType.Ed25519;
    assert sdkInfo.getStream().getConfig().getClass() == MqttConfig.class;

  } 
}
