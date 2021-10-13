
/*******************************************************************************
 * Copyright 2021 Dell Inc.
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
package com.alvarium.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.alvarium.utils.Encoder;

class Sha256Provider implements HashProvider {

  private final MessageDigest sha256;

  protected Sha256Provider() throws HashTypeException {
    try {
      sha256 = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new HashTypeException("SHA-256 provider is not supported");
    }
  }

  // returns the hexadecimal sha256 hash representation of the given data
  public String derive(byte[] data) {
    final byte[] hashedBytes = this.sha256.digest(data);
    final String hashedString = Encoder.bytesToHex(hashedBytes);
    return hashedString; 
  }
}
