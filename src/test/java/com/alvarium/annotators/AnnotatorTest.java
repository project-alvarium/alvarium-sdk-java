package com.alvarium.annotators;

import java.util.HashMap;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
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
    final AnnotatorFactory factory = new AnnotatorFactory();
    final Annotator annotator = factory.getAnnotator(AnnotationType.MOCK, HashType.MD5Hash,
        signature);
    final byte[] data = "test data".getBytes();
    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<>());
    final Annotation annotation = annotator.execute(ctx, data);

    System.out.println(annotation.toJson());
  }  
}
