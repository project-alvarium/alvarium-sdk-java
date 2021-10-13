
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
package com.alvarium.annotators;

import java.util.HashMap;

import com.alvarium.SdkInfo;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashInfo;
import com.alvarium.hash.HashType;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;

import org.junit.Test;

public class AnnotatorTest {
  @Test
  public void mockAnnotatorShouldReturnAnnotation() throws AnnotatorException {
    final KeyInfo keyInfo = new KeyInfo("path", SignType.none);
    final SignatureInfo signature = new SignatureInfo(keyInfo, keyInfo);
    final AnnotationType[] annotators = {};
    final SdkInfo config = new SdkInfo(annotators, new HashInfo(HashType.NoHash), signature, null);
    final AnnotatorFactory factory = new AnnotatorFactory();
    final Annotator annotator = factory.getAnnotator(AnnotationType.MOCK, config);
    final byte[] data = "test data".getBytes();
    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<>());
    final Annotation annotation = annotator.execute(ctx, data);

    System.out.println(annotation.toJson());
  }  
}
