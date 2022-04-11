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

import org.apache.http.client.methods.HttpPost;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

public class ParseResultTest {

  HttpPost getRequest() {
    HttpPost request = new HttpPost(URI.create("http://example.com/foo?var1=&var2=2"));
    request.setHeader("Date", "Tue, 20 Apr 2021 02:07:55 GMT");
    request.setHeader("Content-Type", "application/json");
    request.setHeader("Content-Length", "18");
    request.setHeader("Signature-Input", "");
    request.setHeader("Signature", "whatever");
    return request;
  }

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testIntegrationAllFields() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input",
        "\"date\" \"@method\" \"@path\" \"@authority\" \"content-type\" \"content-length\" \"@query-params\" \"@query\" \"@target-uri\";created=1644758607;keyid=\"public.key\";alg=\"ed25519\";");
    String expectedSeed = "\"date\" Tue, 20 Apr 2021 02:07:55 GMT\n\"@method\" POST\n\"@path\" /foo\n\"@authority\" example.com\n\"content-type\" application/json\n\"content-length\" 18\n\"@query-params\";name=\"var1\": \n\"@query-params\";name=\"var2\": 2\n\"@query\" ?var1=&var2=2\n\"@target-uri\" http://example.com/foo?var1=&var2=2\n;created=1644758607;keyid=\"public.key\";alg=\"ed25519\";";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testExtraSpaces() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Date", "     Tue,     20     Apr      2021 02:07:55  GMT           ");
    request.setHeader("Signature-Input", "\"date\";created=1644758607;keyid=\"public.key\";alg=\"ed25519\";");
    String expectedSeed = "\"date\" Tue, 20 Apr 2021 02:07:55 GMT\n;created=1644758607;keyid=\"public.key\";alg=\"ed25519\";";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testMethodField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@method\";");
    String expectedSeed = "\"@method\" POST\n;";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testAuthorityField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@authority\";");
    String expectedSeed = "\"@authority\" example.com\n;";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testSchemeField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@scheme\";");
    String expectedSeed = "\"@scheme\" http\n;";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testTargetURIField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@target-uri\";");
    String expectedSeed = "\"@target-uri\" http://example.com/foo?var1=&var2=2\n;";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testPathField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@path\";");
    String expectedSeed = "\"@path\" /foo\n;";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testQueryField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@query\";");
    String expectedSeed = "\"@query\" ?var1=&var2=2\n;";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testQueryParamsField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@query-params\";");
    String expectedSeed = "\"@query-params\";name=\"var1\": \n\"@query-params\";name=\"var2\": 2\n;";
    ParseResult parsed = new ParseResult(request);
    assertEquals(expectedSeed, parsed.getSeed());
  }

  @Test
  public void testNonExistantSpecialityComponent() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@x-test\";");
    exceptionRule.expect(ParseResultException.class);
    exceptionRule.expectMessage("Unhandled Derived Component @x-test");
    new ParseResult(request);
  }

  @Test
  public void testNonExistantHeaderField() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"x-test\";");
    exceptionRule.expect(ParseResultException.class);
    exceptionRule.expectMessage("Header field not found x-test");
    new ParseResult(request);
  }

  @Test
  public void testSignature() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@query\";created=1644758607;keyid=\"public.key\";alg=\"ed25519\";");
    ParseResult parsed = new ParseResult(request);
    assertEquals("whatever", parsed.getSignature());
  }

  @Test
  public void testKeyid() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@query\";created=1644758607;keyid=\"public.key\";alg=\"ed25519\";");
    ParseResult parsed = new ParseResult(request);
    assertEquals("public.key", parsed.getKeyid());
  }

  @Test
  public void testAlgorithm() throws URISyntaxException, ParseResultException {
    HttpPost request = getRequest();
    request.setHeader("Signature-Input", "\"@query\";created=1644758607;keyid=\"public.key\";alg=\"ed25519\";");
    ParseResult parsed = new ParseResult(request);
    assertEquals("ed25519", parsed.getAlgorithm());
  }
}
