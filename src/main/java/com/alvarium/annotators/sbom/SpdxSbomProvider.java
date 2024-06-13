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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.jacksonstore.MultiFormatStore.Format;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxCreatorInformation;
import org.spdx.library.model.SpdxDocument;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.utility.compare.SpdxCompareException;
import org.spdx.utility.compare.SpdxComparer;

public class SpdxSbomProvider implements SbomProvider {
  private final String version;

  SpdxSbomProvider(SbomAnnotatorConfig cfg) {
    this.version = cfg.getSbomVersion();
  }

  public boolean exists(String filePath) {
    final File sbom = new File(filePath);
    return sbom.exists();
  }

  public boolean validate(String filePath) throws SbomException {
    try (ISerializableModelStore modelStore =
        new MultiFormatStore(new InMemSpdxStore(), this.getFormat(filePath))) {
      // deSerialize() throws if object has invalid format
      SpdxDocument doc = this.deserializeSpdxFile(filePath, modelStore);
      String docVersion = doc.getSpecVersion();
      if (!docVersion.equals(this.version)) {
        return false;
      }
    } catch (InvalidSPDXAnalysisException e) {
      throw new SbomException("Could not validate SBOM", e);
    } catch (Exception e) {
      throw new SbomException("Could not validate SBOM", e);
    }
    return true;
  }

  public boolean matchesBuild(String filePath, String sourceCodeRootDirPath)
      throws SbomException {
    try (ISerializableModelStore modelStore =
        new MultiFormatStore(new InMemSpdxStore(), this.getFormat(filePath))) {
      SpdxDocument originalSbom = this.deserializeSpdxFile(filePath, modelStore);
      Optional<String> sbomName = originalSbom.getName();
      String pkgName = sbomName.get().split(" ")[0];
      String pkgVersion = sbomName.get().split(" ")[1];

      SpdxCreatorInformation a = originalSbom.getCreationInfo();
      if (a == null) {
        throw new SbomException("SPDX creator info required");
      }

      String creator = a.getCreators().iterator().next();
      if (creator == null) {
        throw new SbomException("SPDX creator info cannot be empty");
      }

      String supplierName = creator.split(" ")[1];
      String sbomLocation = ".";
      String cmd =
          String.format(
              "sbom-tool generate -b %s -bc %s -nsb http://alvarium.com -pn %s -pv %s"
                  + " -ps %s -D true",
              sbomLocation, sourceCodeRootDirPath, pkgName, pkgVersion, supplierName);
      Process sbomGenerationProcess = Runtime.getRuntime().exec(cmd);
      int exitCode = sbomGenerationProcess.waitFor();
      if (exitCode != 0) {
        throw new SbomException("SBoM generation failed with exit code " + exitCode);
      }

      SpdxDocument generatedSbom =
          this.deserializeSpdxFile("_manifest/spdx_2.2/manifest.spdx.json", modelStore);
      SpdxComparer comparer = new SpdxComparer();
      comparer.compare(originalSbom, generatedSbom);
      boolean isBuildMissingPkg = comparer.getUniquePackages(1, 0).size() != 0;
      boolean buildHasAdditionalPkg = comparer.getUniquePackages(0, 1).size() != 0;
      if (isBuildMissingPkg || buildHasAdditionalPkg) {
        return false;
      }
    } catch (InvalidSPDXAnalysisException e) {
      throw new SbomException("Invalid source code SBoM ", e);
    } catch (SpdxCompareException e) {
      return false;
    } catch (IOException e) {
      throw new SbomException("Could not read source code SBoM", e);
    } catch (InterruptedException e) {
      throw new SbomException("Could not read source code SBoM", e);
    } catch (Exception e) {
      throw new SbomException("Could not read source code SBoM", e);
    }
    return true;
  }

  public Format getFormat(String fileName) throws SbomException {
    String[] parts = fileName.split("\\.");
    String extension = parts[parts.length - 1];
    switch (extension) {
      case "json":
        return Format.JSON;
      default:
        throw new SbomException("Unimplemented SPDX file format: " + extension);
    }
  }

  private SpdxDocument deserializeSpdxFile(String filePath, ISerializableModelStore modelStore)
      throws SbomException {
    try (InputStream stream = new FileInputStream(filePath); ) {
      String documentUri = modelStore.deSerialize(stream, false);
      SpdxDocument doc = new SpdxDocument(modelStore, documentUri, null, false);
      return doc;
    } catch (FileNotFoundException e) {
      throw new SbomException("No SPDX file found", e);
    } catch (IOException e) {
      throw new SbomException("Could not read SPDX file", e);
    } catch (InvalidSPDXAnalysisException e) {
      throw new SbomException("Invalid SPDX file", e);
    } catch (Exception e) {
      throw new SbomException("Could not validate SPDX file", e);
    }
  }
}
