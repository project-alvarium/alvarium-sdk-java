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

package com.alvarium.annotators.sbom;

import org.junit.Test;

public class SpdxSbomProviderTest {

  // Valid spdx file with version 2.2
  final String validSpdxPath = "./src/test/java/com/alvarium/annotators/sbom/spdx-valid.json";
  
  // Path points to invalid spdx file
  final String invalidSpdxPath = "./src/test/java/com/alvarium/annotators/sbom/spdx-invalid.json";

  // File path points to does not exist
  final String nonExistantSpdxPath = "./src/test/java/com/alvarium/annotators/sbom/spdx-does-not-exist.json";

  // Path to an spdx file with a different sbom than `validSpdxPath`
  final String differentSpdxFilePath = "./src/test/java/com/alvarium/annotators/sbom/spdx-different.json";


  @Test
  public void existsShouldCheckSpdxOnFileSystem() throws Exception {
    final SbomAnnotatorConfig cfg = new SbomAnnotatorConfig(SbomType.SPDX, "SPDX_2.2");
    SpdxSbomProvider spdx = new SpdxSbomProvider(cfg);

    assert spdx.exists(validSpdxPath);
    assert !spdx.exists(nonExistantSpdxPath);

  }

  @Test
  public void validateShouldFailOnVersionMismatch() throws Exception {
    // Version in config does not match file
    SbomAnnotatorConfig cfg = new SbomAnnotatorConfig(SbomType.SPDX, "SPDX-2.3");
    SpdxSbomProvider spdx = new SpdxSbomProvider(cfg);
    assert !spdx.validate(validSpdxPath);

    cfg = new SbomAnnotatorConfig(SbomType.SPDX, "SPDX-2.2");
    spdx = new SpdxSbomProvider(cfg);
    assert spdx.validate(validSpdxPath);
  }

  @Test
  public void validateShouldThrowOnBadSpdx() throws Exception {
    SbomAnnotatorConfig cfg = new SbomAnnotatorConfig(SbomType.SPDX, "SPDX-2.2");
    SpdxSbomProvider spdx = new SpdxSbomProvider(cfg);
    assert spdx.validate(validSpdxPath);
    try {
      spdx.validate(invalidSpdxPath);
    } catch(Exception e) {
      assert e instanceof SbomException;
    }
  }

  /*
  * This tests an existing SPDX sbom file against the current project. any changes to the 
  * packages in the pom.xml file will result in this test failing and would require a new
  * SPDX file be generated and replace spdx-valid.json
  */
  @Test
  public void matchesBuildShouldFailOnDifferentComposition() throws Exception {
    SbomAnnotatorConfig cfg = new SbomAnnotatorConfig(SbomType.SPDX, "SPDX-2.2");
    SpdxSbomProvider spdx = new SpdxSbomProvider(cfg);
    assert spdx.matchesBuild(validSpdxPath, ".");
    assert !spdx.matchesBuild(differentSpdxFilePath, ".");
  }

}
