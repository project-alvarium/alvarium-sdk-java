/*******************************************************************************
* Copyright 2024 Dell Inc.
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
package com.alvarium.contracts;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import com.alvarium.hash.HashType;

import org.junit.Test;

public class AnnotationTest {
  private final String testJson;
  private final Instant testTimestamp;
  private final String testId;

  public AnnotationTest() throws IOException {
    String path = "./src/test/java/com/alvarium/contracts/data.json";
    testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);

    testId = "01FE0RFFJY94R1ER2AM9BG63E2";
    testTimestamp =
        DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(
            "2021-08-24T12:22:33.334070489-05:00", Instant::from);
  }

  @Test
  public void toJsonShouldReturnTheRightRepresentation() {
    Annotation annotation =
        new Annotation(
            "key",
            HashType.MD5Hash,
            "host",
            LayerType.Application,
            AnnotationType.TPM,
            "signature",
            true,
            testTimestamp);
    String result = annotation.toJson();
    System.out.println(result);
  }

  @Test
  public void fromJsonShouldReturnAnObjectWithTheRightProps() {

    Annotation annotation = Annotation.fromJson(this.testJson);
    assertEquals(this.testId, annotation.getId());
    assertEquals("320", annotation.getKey());
    assertEquals(HashType.MD5Hash, annotation.getHash());
    assertEquals("host", annotation.getHost());
    assertEquals(AnnotationType.TPM, annotation.getKind());
    assertEquals("test sign", annotation.getSignature());
    assertEquals(true, annotation.getIsSatisfied());
    assertEquals(testTimestamp, annotation.getTimestamp());
  }
}
