package com.alvarium.sign;


import org.junit.Test;  
import static org.junit.Assert.*;

import com.alvarium.utils.Encoder;
import com.google.crypto.tink.subtle.Ed25519Sign;

public class SignProviderTest {

  @Test(expected = SignException.class)
  public void factoryShouldReturnNoConcreteTypesError() throws SignException {
    final SignProviderFactory factory = new SignProviderFactory();
    factory.getProvider(SignType.none);   
  }

  @Test
  public void factoryShouldReturnEd25519() throws SignException {
    final SignProviderFactory factory = new SignProviderFactory();
    final SignProvider ed25519Provider = factory.getProvider(SignType.Ed25519);
    assertEquals(ed25519Provider.getClass(), Ed25519Provider.class);
  }

  @Test
  public void signAndVerifyShouldVerifyTrue()  throws Exception {
    final Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
    final byte[] privateKey = keyPair.getPrivateKey();
    final byte[] publicKey = keyPair.getPublicKey();
  
    byte[] content = "hello".getBytes();

    final SignProviderFactory factory = new SignProviderFactory();
    final SignProvider signProvider = factory.getProvider(SignType.Ed25519);

    final String signedString = signProvider.sign(privateKey, content);
    final byte[] signed = Encoder.hexToBytes(signedString);
    signProvider.verify(publicKey, content, signed);
  }

  @Test(expected = SignException.class)
  public void signAndVerifyShouldVerifyFalse() throws Exception {
    final Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
    final Ed25519Sign.KeyPair wrongKeyPair = Ed25519Sign.KeyPair.newKeyPair();
    final byte[] privateKey = keyPair.getPrivateKey();
    final byte[] wrongPublicKey = wrongKeyPair.getPublicKey();

    byte[] content = "foo".getBytes();

    final SignProviderFactory factory = new SignProviderFactory();
    final SignProvider signProvider = factory.getProvider(SignType.Ed25519);

    final String signedString = signProvider.sign(privateKey, content);
    final byte[] signed = Encoder.hexToBytes(signedString);
    signProvider.verify(wrongPublicKey, content, signed);    
  }

}
