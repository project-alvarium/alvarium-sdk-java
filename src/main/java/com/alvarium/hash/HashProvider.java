
/*******************************************************************************
 * Copyright 2023 Dell Inc.
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

/**
 * A unit that provides arbitrary ways to derive hash values
 * from a given piece of data
 */
public interface HashProvider {
  /**
   * converts a byte array of data to it's hash value 
   * @param data byte array of data
   * @return hashed value of the given data
   */
  String derive(byte[] data);

  /**
   * Updates the hash with new input data
   * @param data byte array of data
   * @return new hashed value of the given data
   */
  void update(byte[] data);

  /**
   * Gets the current hash, resets any saved values from previous <code>update()</code>
   * calls
   * @return the final hashing result of previous <code>update()</code> calls 
   */
  String getValue();
}
