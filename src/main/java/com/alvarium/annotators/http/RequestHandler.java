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
package com.alvarium.annotators.http;

import java.util.Date;

import com.alvarium.sign.SignatureInfo;

/**
 * A unit that handles the incoming HTTP request to conform with the SDK's
 * requirments.
 */
public interface RequestHandler {
  /**
   * AddSignatureHeaders takes time of creation of request, the fields to be taken
   * into consideration for the SignatureInput header, and the keys used in
   * signing.
   * Assembles the SignatureInput and Signature fields, then adds them to the
   * request as headers.
   *
   * @param ticks  Date of creation of request
   * @param fields Array of the fields to be taken into consideration for the
   *               SignatureInput header
   * @param keys   Signature info to use in signing the seed
   */
  void addSignatureHeaders(Date ticks, String[] fields, SignatureInfo keys)
      throws RequestHandlerException;
}
