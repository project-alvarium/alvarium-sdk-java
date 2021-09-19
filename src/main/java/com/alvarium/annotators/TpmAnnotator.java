package com.alvarium.annotators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

class TpmAnnotator extends AnnotatorUtils implements Annotator {
  private final HashType hash;
  private final AnnotationType kind;
  private final SignatureInfo signature;
  private final String directTpmPath = "/dev/tpm0";
  private final String tpmKernelManagedPath = "/dev/tpmrm0";

  protected TpmAnnotator(HashType hash, SignatureInfo signature) {
    this.hash = hash;
    this.signature = signature;
    this.kind = AnnotationType.TPM;
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    
    final String key = super.deriveHash(hash, data);
    String host;
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new AnnotatorException("Cannot get host name.", e);
    }

    // Checks whether the TPM driver is accessible through the kernel resource manager, and if that 
    // failes, checks if the TPM driver can be accessed directly
    final Boolean isSatisfied = checkTpmExists(this.tpmKernelManagedPath) ||
       checkTpmExists(this.directTpmPath);

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
   * Checks whether the TPM driver exists (can be accessed) or not, this check was found on the 
   * Microsoft TSS.MSR repository found here 
   * https://github.com/microsoft/TSS.MSR/blob/d715b/TSS.Java/src/tss/TpmDeviceLinux.java
   * 
   * @param devName the tpm path
   * @return True if TPM found, false otherwise
   * @throws AnnotatorException
   */
  private Boolean checkTpmExists(String devName) throws AnnotatorException {
    final RandomAccessFile devTpm;
    final File devTpm0 = new File(devName);
    if (!devTpm0.exists()) {
      return false;
    }
    try {
        devTpm = new RandomAccessFile(devName, "rwd");
        devTpm.close();
        return true;
    } catch (FileNotFoundException e) {
        return false;
    } catch (IOException e) {
      throw new AnnotatorException("Could not close tpm file", e);
    }
  }
}
