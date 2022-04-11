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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alvarium.contracts.DerivedComponent;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;

public class ParseResult {
  private String seed;
  private String signature;
  private String keyid;
  private String algorithm;

  public ParseResult(HttpUriRequest request) throws URISyntaxException, ParseResultException {
    String signatureInput = request.getHeaders("Signature-Input")[0].getValue();

    // to avoid throwing an error when the parser is called from the handler,
    // no signature is present unlike when its called from the annotator
    this.signature = "";
    if (request.containsHeader("Signature")) {
      this.signature = request.getHeaders("Signature")[0].getValue();
    }

    String[] signatureInputList = signatureInput.split(";", 2);
    String[] signatureInputHeader = signatureInputList[0].split("\\s+");
    String signatureInputTail = signatureInputList[1];
    String[] signatureInputParsedTail = signatureInputTail.split(";", -1);

    for (String s : signatureInputParsedTail) {
      if (s.contains("alg")) {
        String raw = s.split("=", -1)[1];
        this.algorithm = raw.substring(1, raw.length() - 1);
      }
      if (s.contains("keyid")) {
        String raw = s.split("=", -1)[1];
        this.keyid = raw.substring(1, raw.length() - 1);
      }
    }

    Map<String, String[]> signatureInputFields = new HashMap<>();

    StringBuilder signatureInputBody = new StringBuilder();

    for (String field : signatureInputHeader) {
      String key = field.substring(1, field.length() - 1);

      if (key.charAt(0) == '@') {
        URI uri = request.getURI();
        DerivedComponent derivedComponent;
        try {
          derivedComponent = DerivedComponent.fromString(key);
        } catch (EnumConstantNotPresentException e) {
          throw new ParseResultException(String.format("Unhandled Derived Component %s", key));
        }
        if (!uri.isAbsolute()) {
          throw new ParseResultException("Request URI is not absolute");
        }
        switch (derivedComponent) {
          case METHOD:
            signatureInputFields.put(key, new String[] { request.getMethod() });
            break;
          case TARGETURI:
            signatureInputFields.put(key, new String[] { uri.toString() });
            break;
          case AUTHORITY:
            signatureInputFields.put(key, new String[] { uri.getAuthority() });
            break;
          case SCHEME:
            signatureInputFields.put(key, new String[] { uri.getScheme() });
            break;
          case PATH:
            signatureInputFields.put(key, new String[] { uri.getPath() });
            break;
          case QUERY:
            String query = "?" + uri.getRawQuery();
            signatureInputFields.put(key, new String[] { query });
            break;
          case QUERYPARAMS:
            String[] rawQueryParams = uri.getRawQuery().split("&", -1);
            List<String> queryParams = new ArrayList<>();
            for (String rawQueryParam : rawQueryParams) {
              if (rawQueryParam != "") {
                String[] parameter = rawQueryParam.split("=", -1);
                String name = parameter[0];
                String value = parameter[1];
                queryParams.add(String.format(";name=\"%s\": %s", name, value));
              }
            }
            signatureInputFields.put(key, queryParams.toArray(new String[queryParams.size()]));
        }
      } else {
        Header[] fieldValues = request.getHeaders(key);
        if (fieldValues.length == 0) {
          throw new ParseResultException(String.format("Header field not found %s", key));
        } else {
          StringBuilder value = new StringBuilder();
          for (int i = 0; i < fieldValues.length - 1; i++) {
            value.append(fieldValues[i].getValue() + ", ");
          }
          value.append(fieldValues[fieldValues.length - 1].getValue());

          // Remove extra spaces from fieldValue
          String fieldValue = String.join(" ", value.toString().trim().split("\\s+"));
          signatureInputFields.put(key, new String[] { fieldValue });
        }
      }

      // Construct final output string
      String[] keyValues = signatureInputFields.get(key);
      if (keyValues.length == 1) {
        signatureInputBody.append("\"" + key + "\" " + keyValues[0] + "\n");
      } else {
        for (String s : keyValues) {
          signatureInputBody.append("\"" + key + "\"" + s + "\n");
        }
      }
    }

    this.seed = String.format("%s;%s", signatureInputBody.toString(), signatureInputTail);

    return;
  }

  public String getSeed() {
    return this.seed;
  }

  public String getSignature() {
    return this.signature;
  }

  public String getKeyid() {
    return this.keyid;
  }

  public String getAlgorithm() {
    return this.algorithm;
  }
}
