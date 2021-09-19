package com.alvarium.annotators;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;
import com.alvarium.sign.SignProviderFactory;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.Encoder;
import com.alvarium.utils.PropertyBag;

class PkiAnnotator extends AnnotatorUtils implements Annotator {
  private final HashType hash;
  private final SignatureInfo signature;
  private final AnnotationType kind;

  protected PkiAnnotator(HashType hash, SignatureInfo signature) {
    this.hash = hash;
    this.signature = signature;
    this.kind = AnnotationType.PKI;
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    final String key = super.deriveHash(hash, data);
    
    String host;
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new AnnotatorException("Cannot get host name", e);
    }  

    final Signable signable = Signable.fromJson(new String(data));

    Boolean isSatisfied = verifySignature(signature.getPublicKey(), signable);
    
    final Annotation annotation = new Annotation(
        key, 
        hash, 
        host, 
        kind, 
        null, 
        isSatisfied, 
        Instant.now());

    final String annotationSignature = super.signAnnotation(signature.getPrivateKey(), annotation);
    annotation.setSignature(annotationSignature);
    return annotation;

  }
  
  /**
   * Responsible for verifying the signature, returns true if the verification passed, false 
   * otherwise.
   * 
   * 
   * @param key The public key used to verify the signature
   * @param signable Contains the data (seed) and signature
   * @return True if signature valid, false otherwise
   * @throws AnnotatorException
   */
  private Boolean verifySignature(KeyInfo key, Signable signable) throws AnnotatorException {
    final SignProviderFactory signFactory = new SignProviderFactory();
    final SignProvider signProvider;
    try {
      signProvider = signFactory.getProvider(key.getType());
    } catch(SignException e) {
      throw new AnnotatorException("Could not instantiate signing provider", e);
    }

    try {
      // Load public key
      final String publicKeyPath = key.getPath();
      final String publicKey = Files.readString(
          Paths.get(publicKeyPath), 
          StandardCharsets.US_ASCII);

      // Verify signature
      signProvider.verify(
          Encoder.hexToBytes(publicKey), 
          signable.getSeed().getBytes(), 
          Encoder.hexToBytes(signable.getSignature()));

      return true;
    } catch(SignException e) {
        return false;     
    } catch(IOException e) {
      throw new AnnotatorException("Failed to load public key", e);
    }

  }
}
