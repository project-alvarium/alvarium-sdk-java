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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PkiAnnotatorTest {
  @Test
  public void executeShouldGetSatisfiedAnnotation() throws AnnotatorException {
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", 
        SignType.Ed25519);
    final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
        SignType.Ed25519);
    final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<String, Object>());

    final String signature = "B9E41596541933DB7144CFBF72105E4E53F9493729CA66331A658B1B18AC6DF5DA991"
        +"AD9720FD46A664918DFC745DE2F4F1F8C29FF71209B2DA79DFD1A34F50C";

    final byte[] data = String.format("{seed: \"helloo\", signature: \"%s\"}", signature)
        .getBytes();

    final Annotator annotator = annotatorFactory.getAnnotator(
        AnnotationType.PKI, 
        HashType.SHA256Hash,
        sigInfo);
    final Annotation annotation = annotator.execute(ctx, data);
    assertTrue("isSatisfied should be true", annotation.getIsSatisfied());
  }

  @Test
  public void executeShouldGetUnsatisfiedAnnotation() throws AnnotatorException {
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", 
        SignType.Ed25519);
    final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
        SignType.Ed25519);
    final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

    final PropertyBag ctx = new ImmutablePropertyBag(new HashMap<String, Object>());

    final String signature = "A9E41596541933DB7144CFBF72105E4E53F9493729CA66331A658B1B18AC6DF5DA991"
        +"AD9720FD46A664918DFC745DE2F4F1F8C29FF71209B2DA79DFD1A34F50C";

    final byte[] data = String.format("{seed: \"helloo\", signature: \"%s\"}", signature)
        .getBytes();

    final Annotator annotator = annotatorFactory.getAnnotator(
        AnnotationType.PKI, 
        HashType.SHA256Hash,
        sigInfo);
    final Annotation annotation = annotator.execute(ctx, data);
    assertFalse("isSatisfied should be false", annotation.getIsSatisfied());
  }
}
