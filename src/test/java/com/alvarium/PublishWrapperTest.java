
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationList;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.LayerType;
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
    final Annotation annotation = new Annotation(
      "key", 
      HashType.MD5Hash, 
      "host", 
      "tag",
      LayerType.Application,
      AnnotationType.TPM, 
      "signature", 
      true, 
      Instant.now()
    );
    final List<Annotation> annotations = List.of(annotation, annotation);
    final PublishWrapper wrapper = new PublishWrapper(
        action,
        messageType, 
        new AnnotationList(annotations)
    );
    System.out.println(wrapper.toJson());
  }
}
