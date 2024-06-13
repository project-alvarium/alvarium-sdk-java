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
package com.alvarium.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.alvarium.utils.Encoder;

class Md5Provider implements HashProvider {
  private final MessageDigest md5;

  protected Md5Provider() throws HashTypeException {
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new HashTypeException("MD5 provider is not supported");
    }
  }

  @Override
  public String derive(byte[] data) {
    final byte[] hashedBytes = this.md5.digest(data);
    return Encoder.bytesToHex(hashedBytes);
  }

  @Override
  public void update(byte[] data) {
    this.md5.update(data);
  }

  @Override
  public void update(byte[] data, int offset, int size) {
    this.md5.update(data, offset, size);
  }

  @Override
  public String getValue() {
    final String hashedString = Encoder.bytesToHex(this.md5.digest());
    return hashedString;
  }
}
