package com.alvarium.sign;

import java.security.GeneralSecurityException;

import com.alvarium.utils.Encoder;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.Ed25519Verify;


public class Ed25519Provider implements SignProvider {

  protected Ed25519Provider() {}

  public String sign(byte[] key, byte[] content) throws SignException {
    
    final Ed25519Sign signer;

    try {
      signer = new Ed25519Sign(key);
    } catch(GeneralSecurityException e) {
      throw new SignException("SHA-512 not defined in EngineFactory.MESSAGE_DIGEST", e);
    } catch(IllegalArgumentException e) {
      throw new SignException("Invalid signing key", e);
    } catch (Exception e) {
      throw new SignException("Could not instantiate Ed25519Provider", e);
    }

    try {
      final byte[] signed = signer.sign(content);
      final String signedString = Encoder.bytesToHex(signed);
      return signedString;
    } catch(GeneralSecurityException e) {
      throw new SignException("Could not sign data", e);
    } catch(Exception e) {
      throw new SignException("Could not sign data", e);
    }
  }

  public void verify(byte[] key, byte[] content, byte[] signed) throws SignException {
    try {
      final Ed25519Verify verifier = new Ed25519Verify(key);
      verifier.verify(signed, content);
    } catch(GeneralSecurityException e) {
      throw new SignException("Verification did not pass", e);
    } catch(IllegalArgumentException e) {
      throw new SignException("Invalid signing key", e);
    } catch (Exception e) {
      throw new SignException("Could not verify signature", e);
    }
  }

}
