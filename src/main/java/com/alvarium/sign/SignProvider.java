
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
package com.alvarium.sign;

public interface SignProvider {
  /**
   * Uses a private key to sign content and returns the signature in hex format stored in a string
   * 
   * The key parameter needs to be 64-byte in size where the private key is suffixed by 
   * the public key (Some implementations will accept and use only the first 32-bytes of the
   * private key such as Ed25519)
   *  
   * @param key
   * @param content
   * @return signature
   * @throws SignException
   */
  String sign(byte[] key, byte[] content) throws SignException;

  /**
   * Verifies a signature against content using a public key
   * @param key
   * @param content
   * @param signed
   * @throws SignException when verification does not pass
   */
  void verify(byte[] key, byte[] content, byte[] signed) throws SignException;
} 
