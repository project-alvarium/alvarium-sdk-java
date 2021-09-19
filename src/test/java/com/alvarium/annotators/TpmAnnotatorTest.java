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

public class TpmAnnotatorTest {
  
  @Test
  public void executeShouldCreateAnnotation() throws AnnotatorException {
    AnnotatorFactory factory = new AnnotatorFactory();
    KeyInfo privateKey = new KeyInfo(
        "./src/test/java/com/alvarium/annotators/public.key",
        SignType.Ed25519);
    KeyInfo publicKey = new KeyInfo(
        "./src/test/java/com/alvarium/annotators/public.key", 
        SignType.Ed25519);

    SignatureInfo sign = new SignatureInfo(publicKey, privateKey);
    Annotator tpm = factory.getAnnotator(AnnotationType.TPM, HashType.MD5Hash, sign);
    
    PropertyBag ctx = new ImmutablePropertyBag(new HashMap<String, Object>());
    
    byte[] data = {0x1, 0x2};
    Annotation annotation = tpm.execute(ctx, data);
    System.out.println(annotation.toJson());
  }
  
}
