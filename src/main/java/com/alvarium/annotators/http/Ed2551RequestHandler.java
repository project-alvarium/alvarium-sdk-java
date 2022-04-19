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

import java.io.IOException;
import java.lang.StringBuilder;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Date;

import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;
import com.alvarium.sign.SignProviderFactory;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.Encoder;

import org.apache.http.client.methods.HttpUriRequest;

public class Ed2551RequestHandler implements RequestHandler {

	HttpUriRequest request;

	public Ed2551RequestHandler(HttpUriRequest request) {
		this.request = request;
	}

	public void addSignatureHeaders(Date ticks, String[] fields, SignatureInfo keys)
			throws RequestHandlerException {

		if (fields.length == 0) {
			throw new RequestHandlerException("No fields found to be used in generating the seed");
		}

		// This will be the value returned for populating the Signature-Input header
		StringBuilder headerValue = new StringBuilder();
		for (int i = 0; i < fields.length - 1; i++) {
			headerValue.append(String.format("\"%s\" ", fields[i]));
		}

		headerValue.append(String.format("\"%s\"", fields[fields.length - 1]));

		String timeInSeconds = Long.toString((ticks.getTime() / 1000));
		String tail = String.format(
				";created=%s;keyid=\"%s\";alg=\"%s\";",
				timeInSeconds,
				Paths.get(keys.getPublicKey().getPath()).getFileName(),
				keys.getPublicKey().getType().getValue());

		headerValue.append(tail);

		request.setHeader("Signature-Input", headerValue.toString());

		ParseResult parseResult;
		try {
			parseResult = new ParseResult(request);
		} catch (URISyntaxException e) {
			throw new RequestHandlerException("Invalid request URI", e);

		} catch (ParseResultException e1) {
			throw new RequestHandlerException("Error parsing the request", e1);
		}

		// This will be the value used as input for the signature
		String inputValue = parseResult.getSeed();

		final SignProviderFactory signFactory = new SignProviderFactory();
		SignProvider signProvider;
		try {
			signProvider = signFactory.getProvider(SignType.Ed25519);
		} catch (SignException e) {
			throw new RequestHandlerException("Invalid key type", e);
		}

		String privateKeyPath = "";
		try {
			privateKeyPath = Files.readString(Paths.get(keys.getPrivateKey().getPath()), StandardCharsets.US_ASCII);
		} catch (IOException e) {
			throw new RequestHandlerException("Cannot read key.", e);
		}

		String signature = "";
		try {
			signature = signProvider.sign(Encoder.hexToBytes(privateKeyPath), inputValue.getBytes());
		} catch (SignException e) {
			throw new RequestHandlerException("Cannot sign annotation.", e);
		}
		request.setHeader("Signature", signature);
	}
}
