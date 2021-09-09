package com.alvarium.annotators;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;

import org.junit.Test;

public class TlsAnnotatorTest {
  @Test
  public void executeShouldReturnAnnotation() throws AnnotatorException, IOException,
      UnknownHostException {

    // construct annotator
    final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
    final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key", 
        SignType.Ed25519);
    final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
        SignType.Ed25519);
    final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);
    final Annotator annotator = annotatorFactory.getAnnotator(AnnotationType.TLS, HashType.SHA256Hash,
        sigInfo);
    
    // dummy data
    final byte[] data = "test data".getBytes();

    // create a connect with google servers
    // and provide the SSLSocket to the annotator
    SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    SSLSocket socket = (SSLSocket) sslFactory.createSocket("www.google.com", 443);
    HashMap<String, Object> map = new HashMap<>();
    map.put(AnnotationType.TLS.name(), socket);
    final PropertyBag bag = new ImmutablePropertyBag(map);

    final Annotation annotation = annotator.execute(bag, data);
    System.out.println(annotation.toJson());
  }  
}
