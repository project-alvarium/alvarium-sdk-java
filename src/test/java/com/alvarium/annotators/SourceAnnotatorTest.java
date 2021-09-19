package com.alvarium.annotators;

import java.io.IOException;
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

public class SourceAnnotatorTest {
  @Test
  public void executeShouldReturnAnnotation() throws AnnotatorException, IOException {
    // construct annotator
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", 
        SignType.Ed25519);
    final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
        SignType.Ed25519);
    final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);
    final Annotator annotator = annotatorFactory.getAnnotator(AnnotationType.SOURCE,
        HashType.SHA256Hash, sigInfo);

    // dummy data and empty prop bag
    final byte[] data = "test data".getBytes();
    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<String, Object>());

    final Annotation annotation = annotator.execute(ctx, data);
    System.out.println(annotation.toJson());
  }

}
