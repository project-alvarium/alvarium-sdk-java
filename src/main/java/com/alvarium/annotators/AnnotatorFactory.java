
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
package com.alvarium.annotators;

import com.alvarium.SdkInfo;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;

public class AnnotatorFactory {

  public Annotator getAnnotator(AnnotationType kind, SdkInfo config) throws AnnotatorException {
    final HashType hash = config.getHash().getType();
    final SignatureInfo signature = config.getSignature();
    switch (kind) {
      case MOCK:
        return new MockAnnotator(hash, kind, signature);
      case TLS:
        return new TlsAnnotator(hash, signature);
      case PKI:
        return new PkiAnnotator(hash, signature);
      case PKIHttp:
        return new PkiHttpAnnotator(hash, signature);
      case TPM:
        return new TpmAnnotator(hash, signature);
      case SOURCE:
        return new SourceAnnotator(hash, signature);
      default:
        throw new AnnotatorException("Annotator type is not supported"); 
    }
  }  
}
