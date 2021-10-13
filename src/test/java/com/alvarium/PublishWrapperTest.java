
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
package com.alvarium;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;

import org.junit.Test;

public class PublishWrapperTest {

  @Test
  public void toJsonShouldReturnAppropriateRepresentation() {
    final SdkAction action = SdkAction.CREATE;
    final String messageType = "test type";
    final ArrayList<Integer> content = new ArrayList<Integer>(Arrays.asList(1,2,3,4));     

    final PublishWrapper wrapper = new PublishWrapper(action,messageType, content);
    System.out.println(wrapper.toJson());
  }  

  @Test
  public void toJsonShouldParseAnnotationCorrectly() {
    final SdkAction action = SdkAction.CREATE;
    final String messageType = "test type";
    final Annotation annotation = new Annotation("key", HashType.MD5Hash, "host", AnnotationType.TPM
        , "signature", true, Instant.now());

    final Annotation[] annotationList = {annotation, annotation};    
    final PublishWrapper wrapper = new PublishWrapper(action, messageType, annotationList);
    System.out.println(wrapper.toJson());
  }

  @Test
  public void fromJsonShouldReturnAppropriateObject() throws IOException {
    String path = "./src/test/java/com/alvarium/publishWrapperData.json";
    final String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);

    final PublishWrapper wrapper = PublishWrapper.fromJson(testJson);
    assertEquals(SdkAction.CREATE, wrapper.getAction());
    assertEquals("test type", wrapper.getMessageType()); 
    assertEquals("test content", wrapper.getContent());
   }
}
