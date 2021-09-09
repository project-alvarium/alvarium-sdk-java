package com.alvarium.annotators;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alvarium.contracts.Annotation;
import com.alvarium.hash.HashProvider;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;
import com.alvarium.sign.SignProviderFactory;
import com.alvarium.utils.Encoder;

/**
 * A Util class responsible for carrying out common operations done by the annotators
 */
abstract class AnnotatorUtils {

  /**
   * returns hash of the provided data depending on the given hash type
   * @param type
   * @param data
   * @return
   * @throws AnnotatorException
   */
  protected String deriveHash(HashType type, byte[] data) throws AnnotatorException {
    final HashProviderFactory hashFactory = new HashProviderFactory();
    
    try {
      final HashProvider provider = hashFactory.getProvider(type);
      return provider.derive(data);
    } catch (HashTypeException e) {
      throw new AnnotatorException("cannot hash data.", e);
    }
  }

  /**
   * returns the signature of the given annotation object after converting it to its json
   * representation
   * @param keyInfo
   * @param annotation
   * @return
   * @throws AnnotatorException
   */
  protected String signAnnotation(KeyInfo keyInfo, Annotation annotation) throws 
      AnnotatorException {
    final SignProviderFactory signFactory = new SignProviderFactory();

    try {
      final SignProvider provider = signFactory.getProvider(keyInfo.getType());
      final String key = Files.readString(Paths.get(keyInfo.getPath()),
          StandardCharsets.US_ASCII);
      return provider.sign(Encoder.hexToBytes(key), annotation.toJson().getBytes());      
    } catch (SignException e) {
      throw new AnnotatorException("cannot sign annotation.", e);
    } catch (IOException e) {
      throw new AnnotatorException("cannot read key.", e);
    }
  }
}
