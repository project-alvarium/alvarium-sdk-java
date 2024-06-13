/*******************************************************************************
* Copyright 2024 Dell Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License
* is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
* or implied. See the License for the specific language governing permissions and limitations under
* the License.
*******************************************************************************/
package com.alvarium.annotators;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;
import com.alvarium.sign.SignProviderFactory;
import com.alvarium.utils.Encoder;

import org.apache.logging.log4j.Logger;

abstract class AbstractPkiAnnotator extends AbstractAnnotator {
  AbstractPkiAnnotator(Logger logger) {
    super(logger);
  }

  /**
   * Responsible for verifying the signature, returns true if the verification
   * passed, false otherwise.
   *
   * @param key      The public key used to verify the signature
   * @param signable Contains the data (seed) and signature
   * @return True if signature valid, false otherwise
   * @throws AnnotatorException
   */
  protected Boolean verifySignature(KeyInfo key, Signable signable) throws AnnotatorException {
    final SignProviderFactory signFactory = new SignProviderFactory();
    final SignProvider signProvider;
    try {
      signProvider = signFactory.getProvider(key.getType());
    } catch (SignException e) {
      throw new AnnotatorException("Could not instantiate signing provider", e);
    }

    try {
      // Load public key
      final String publicKeyPath = key.getPath();
      final String publicKey =
          Files.readString(Paths.get(publicKeyPath), StandardCharsets.US_ASCII);

      // Verify signature
      signProvider.verify(
          Encoder.hexToBytes(publicKey),
          signable.getSeed().getBytes(),
          Encoder.hexToBytes(signable.getSignature()));

      return true;
    } catch (SignException e) {
      return false;
    } catch (IOException e) {
      throw new AnnotatorException("Failed to load public key", e);
    }
  }
}
