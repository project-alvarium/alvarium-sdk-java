/*******************************************************************************
 * Copyright 2022 Dell Inc.
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

import org.junit.Test;
import java.net.URI;

import com.alvarium.sign.SignType;

import org.apache.http.client.methods.HttpPost;

import static org.junit.Assert.assertEquals;

public class RequestHandlerTest {

  @Test(expected = RequestHandlerException.class)
  public void factoryShouldReturnHandlerTypeNotFoundError() throws RequestHandlerException {
    HttpPost request = new HttpPost(URI.create("http://example.com/foo?var1=&var2=2"));
    final RequestHandlerFactory factory = new RequestHandlerFactory();
    factory.getRequestHandler(SignType.none, request);
  }

  @Test
  public void factoryShouldReturnEd25519() throws RequestHandlerException {
    HttpPost request = new HttpPost(URI.create("http://example.com/foo?var1=&var2=2"));
    final RequestHandlerFactory factory = new RequestHandlerFactory();
    final RequestHandler ed25519Handler = factory.getRequestHandler(SignType.Ed25519, request);
    assertEquals(ed25519Handler.getClass(), Ed2551RequestHandler.class);
  }
}
