
/*******************************************************************************
 * Copyright 2023 Dell Inc.
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;



import com.alvarium.SdkInfo;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.LayerType;
import com.alvarium.tag.TagWriter;
import com.alvarium.hash.HashInfo;
import com.alvarium.hash.HashProvider;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.serializers.AnnotatorConfigConverter;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class SourceCodeAnnotatorTest {

        @Rule
        public TemporaryFolder dir = new TemporaryFolder();

        @Test
        public void executeShouldReturnAnnotation() throws AnnotatorException, IOException, HashTypeException {
                AnnotatorFactory factory = new AnnotatorFactory();
                KeyInfo privateKey = new KeyInfo(
                                "./src/test/java/com/alvarium/annotators/public.key",
                                SignType.Ed25519);
                KeyInfo publicKey = new KeyInfo(
                                "./src/test/java/com/alvarium/annotators/public.key",
                                SignType.Ed25519);

                SignatureInfo sign = new SignatureInfo(publicKey, privateKey);
                
                final Gson gson = new GsonBuilder()
                .registerTypeAdapter(AnnotatorConfig.class, new AnnotatorConfigConverter())
                .create();
                final String json = "{\"kind\": \"source-code\"}";
                final AnnotatorConfig annotatorInfo = gson.fromJson(
                            json, 
                            AnnotatorConfig.class
                );                 
                final AnnotatorConfig[] annotators = {annotatorInfo};  
                final SdkInfo config = new SdkInfo(annotators, new HashInfo(HashType.MD5Hash), sign, null, LayerType.Application);
                        // init logger
                final Logger logger = LogManager.getRootLogger();
                Configurator.setRootLevel(Level.DEBUG);
                Annotator annotator = factory.getAnnotator(annotatorInfo, config, logger);

                final File sourceCodeDir = dir.newFolder("sourceCode");
                final File f1 = new File(sourceCodeDir, "file1");
                final File subDirectory = new File(sourceCodeDir, "sub");
                final File f2 = new File(subDirectory, "file2");
                subDirectory.mkdir();
                f1.createNewFile();
                f2.createNewFile();
                Files.write(f1.toPath(), "foo".getBytes());
                Files.write(f2.toPath(), "boo".getBytes());
    
                final File checksumFile = dir.newFile("checksum");
    
                final HashProvider hash = new HashProviderFactory().getProvider(config.getHash().getType());
                
                List<String> hashesAndPaths = new ArrayList<>();
                byte[] f1Content = Files.readAllBytes(f1.toPath()) ;
                String f1RelativePath = f1.getPath().replace(sourceCodeDir.getPath(), ".");
                byte[] f2Content = Files.readAllBytes(f2.toPath());
                String f2RelativePath = f2.getPath().replace(sourceCodeDir.getPath(), ".");

                hashesAndPaths.add(hash.derive(f1Content).toLowerCase() + "  " + f1RelativePath);
                hashesAndPaths.add(hash.derive(f2Content).toLowerCase() + "  " + f2RelativePath);

                Collections.sort(hashesAndPaths, Collator.getInstance(Locale.US));

                String hashesAndFiles = String.join("\n", hashesAndPaths) + "\n";
                final String checksum = hash.derive(hashesAndFiles.getBytes()).toLowerCase();
                Files.write(checksumFile.toPath(), checksum.getBytes());
    
                final SourceCodeAnnotatorProps props = 
                        new SourceCodeAnnotatorProps(
                                sourceCodeDir.toPath().toString(), 
                                checksumFile.toPath().toString()
                );

                Map<LayerType, TagWriter> tagWriterOverrides = new HashMap<>();

                tagWriterOverrides.put(LayerType.Application, () -> {
                        return "Custom tag value for Application";
                });

                PropertyBag ctx = new ImmutablePropertyBag(
                        Map.of(AnnotationType.SourceCode.name(), props,
                        "tagWriterOverrides", tagWriterOverrides)
                );

                byte[] data = "pipeline1/1".getBytes();

                Annotation annotation = annotator.execute(ctx, data);
                System.out.println(annotation.toJson());
                assert annotation.getIsSatisfied();

                // tamper with existing file in source code
                Files.write(f1.toPath(), "tampered".getBytes()); 
                
                annotation = annotator.execute(ctx, data);
                System.out.println(annotation.toJson());

                assert !annotation.getIsSatisfied();

        }
}