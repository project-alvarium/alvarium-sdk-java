
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
package com.alvarium.contracts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import com.alvarium.hash.HashType;

import org.junit.Test;

public class AnnotationListTest {
  
  @Test
  public void toJsonShouldParseCorrectly() {
    final Annotation annotation = new Annotation(
      "key", 
      HashType.NoHash,
      "host", 
      "tag",
      AnnotationLayer.MOCK,
      AnnotationType.MOCK, 
      "signature", 
      true, 
      Instant.now()
    );
    final AnnotationList annotations = new AnnotationList(List.of(annotation, annotation));
    System.out.println(annotations.toJson());
  }

  @Test
  public void fromJsonShouldPraseCorrectly() throws IOException {

    final String annotation = "{\"id\":\"01FHX2DH740WQB4WQS6A6X21QP\","+
    "\"key\":\"key\"," +
    "\"hash\":\"md5\"," +
    "\"host\":\"host\"," + 
    "\"kind\":\"tpm\"," + 
    "\"signature\":\"signature\"," +
    "\"isSatisfied\":true," +
    "\"timestamp\":\"2021-10-13T07:55:33.585017-07:00\"}";

    final String annotationsJson = String.format("{\"items\": [%s,%s]}", annotation, annotation);
    final AnnotationList annotationList = AnnotationList.fromJson(annotationsJson);
    assertNotNull(annotationList);
    assertNotNull(annotationList.getAnnotations());
    assertEquals(
      "AnnotationList initialized with incorrect items length",
      annotationList.getAnnotations().size(),
      2
    ); 
  }
}
