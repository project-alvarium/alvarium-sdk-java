
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
package com.alvarium;

import com.alvarium.annotators.AnnotatorException;
import com.alvarium.streams.StreamException;
import com.alvarium.utils.PropertyBag;

public interface Sdk {
  /**
   * Annotates incoming data based on the list of annotators that was provided to the sdk and
   * publishes it to the given StreamProvider.
   * 
   * @param properties : A property bag that may be used by specific (or custom) annotators to pass
   * custom values to them. The built-in annotators that require custom values are
   * <ul>
   * <li>TLS: Takes a key-value pair of "TLS": Socket</li>
   * <li>PKIHttp: Takes a key-value pair of "PKIHttp": Http Request</li>
   * </ul>
   * @param data : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void create(PropertyBag properties, byte[] data) 
      throws AnnotatorException, StreamException;

  /**
   * Annotates incoming data based on the list of annotators that was provided to the sdk and 
   * publishes it to the given StreamProvider.
   * @param data : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void create(byte[] data) throws AnnotatorException, StreamException;

  /**
   * Used when the recieved piece of data is originated by a separate application.
   * The data is being transitioned from one application to another.
   * @param properties : A property bag that may be used by specific (or custom) annotators to pass
   * custom values to them. The built-in annotators that require custom values are
   * <ul>
   * <li>TLS: Takes a key-value pair of "TLS": Socket</li>
   * </ul>
   * @param data : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void transit(PropertyBag properties, byte[] data) 
      throws AnnotatorException, StreamException;

  /**
   * Used when the recieved piece of data is originated by a separate application.
   * The data is being transitioned from one application to another.
   * @param data
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void transit(byte[] data) throws AnnotatorException, StreamException;
  /**
   * Handles annotations related to any type of data modification.
   * @param properties : A property bag that may be used by specific (or custom) annotators to pass
   * custom values to them. The built-in annotators that require custom values are
   * <ul>
   * <li>TLS: Takes a key-value pair of "TLS": Socket</li>
   * </ul>
   * @param oldData : original data
   * @param newData : incoming new data
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) throws 
      AnnotatorException, StreamException;

  /**
   * Handles annotations related to any type of data modification.
   * @throws StreamException
   */
  public void mutate(byte[] oldData, byte[] newData) throws AnnotatorException, StreamException;

  /**
   * Publish is proposed to provide extensibility for annotators that may need
   * to attest to the state of data before it is sent over the wire. Publish could 
   * also be useful in cases where the downstream host receiving the data isn't r
   * running Alvarium-enabled applications.
   * @param properties : A property bag that may be used by specific (or custom)
   *                   annotators to pass custom values to them. The built-in 
   *                   annotators that requires custom values are
   *                   <ul>
   *                   <li>TLS: Takes a key-value pair of "TLS": Socket</li>
   *                   </ul>
   * @param data       : data being annotated
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void publish(PropertyBag properties, byte[] data) 
      throws AnnotatorException, StreamException;

  /**
   * Publish is proposed to provide extensibility for annotators that may need
   * to attest to the state of data before it is sent over the wire. Publish could 
   * also be useful in cases where the downstream host receiving the data isn't
   * running Alvarium-enabled applications.
   * @param data
   * @throws AnnotatorException
   * @throws StreamException
   */
  public void publish(byte[] data) throws AnnotatorException, StreamException;

  /**
   * Closes any open connections 
   * @throws StreamException
   */
  public void close() throws StreamException;
}
