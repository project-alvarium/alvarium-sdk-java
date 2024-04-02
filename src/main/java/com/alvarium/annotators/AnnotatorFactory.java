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
package com.alvarium.annotators;

import org.apache.logging.log4j.Logger;

import com.alvarium.SdkInfo;
import com.alvarium.annotators.sbom.SbomAnnotatorConfig;
import com.alvarium.annotators.vulnerability.VulnerabilityAnnotatorConfig;
import com.alvarium.contracts.LayerType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;

public class AnnotatorFactory {

  public Annotator getAnnotator(AnnotatorConfig cfg, SdkInfo config, Logger logger) throws AnnotatorException {
    final HashType hash = config.getHash().getType();
    final SignatureInfo signature = config.getSignature();
    final LayerType layer = config.getLayer();
    switch (cfg.getKind()) {
      case MOCK:
        try {
            MockAnnotatorConfig mockCfg = MockAnnotatorConfig.class.cast(cfg);
            return new MockAnnotator(mockCfg, hash, signature, layer);
        } catch(ClassCastException e) {
            throw new AnnotatorException("Invalid annotator config", e);
        }
      case TLS:
        return new TlsAnnotator(hash, signature, logger, layer);
      case PKI:
        return new PkiAnnotator(hash, signature, logger, layer);
      case PKIHttp:
        return new PkiHttpAnnotator(hash, signature, logger, layer);
      case TPM:
        return new TpmAnnotator(hash, signature, logger, layer);
      case SourceCode:
        return new SourceCodeAnnotator(hash, signature, logger, layer);
      case CHECKSUM:
        return new ChecksumAnnotator(hash, signature, logger, layer);
      case VULNERABILITY:
        VulnerabilityAnnotatorConfig vulnCfg = VulnerabilityAnnotatorConfig.class.cast(cfg);
        return new VulnerabilityAnnotator(vulnCfg, hash, signature, logger, layer);
      case SOURCE:
        return new SourceAnnotator(hash, signature, logger, layer);
      case SBOM:
        final SbomAnnotatorConfig sbomCfg = SbomAnnotatorConfig.class.cast(cfg);
        return new SbomAnnotator(sbomCfg, hash, signature, logger, layer);
      default:
        throw new AnnotatorException("Annotator type is not supported");
    }
  }
}
