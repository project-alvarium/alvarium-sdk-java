
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;



import com.alvarium.SdkInfo;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashInfo;
import com.alvarium.hash.HashProvider;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
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
        public void executeShouldReturnAnnotation() throws AnnotatorException {
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
                final SdkInfo config = new SdkInfo(annotators, new HashInfo(HashType.MD5Hash), sign, null);
                Annotator annotator = factory.getAnnotator(annotatorInfo, config);

                // Generate dummy source code directory and 
                // generate checksum for dummy source code
                File sourceCodeDir;
                File checksum; 
                File f1;
                try {
                        sourceCodeDir = dir.newFolder("source-code");
                        f1 = new File(sourceCodeDir.toPath().toString() + "/f1");
                        checksum = dir.newFile("checksum");
                } catch (IOException e) {
                        throw new AnnotatorException("Could not create test file", e);
                }
                
                generateAndWriteChecksum(sourceCodeDir, checksum.toPath(), config.getHash().getType());

                final SourceCodeAnnotatorProps props = 
                        new SourceCodeAnnotatorProps(
                                sourceCodeDir.toPath().toString(), 
                                checksum.toPath().toString()
                );

                PropertyBag ctx = new ImmutablePropertyBag(
                        Map.of(AnnotationType.SourceCode.name(), props)
                );

                byte[] data = "pipeline1/1".getBytes();

                Annotation annotation = annotator.execute(ctx, data);
                System.out.println(annotation.toJson());
                assert annotation.getIsSatisfied();

                // tamper with existing file in source code
                try {
                        Files.write(f1.toPath(), "foo".getBytes()); 
                } catch (IOException e) {
                        throw new AnnotatorException("Could not write to test file", e);
                }
                
                annotation = annotator.execute(ctx, data);
                System.out.println(annotation.toJson());

                assert !annotation.getIsSatisfied();

        }


        /**
         * Writes the checksum of a sourceCodeDir to a file located at checksumPath using the same
         * hashing algorithm provided to the source annotator
         * @param sourceCodeDir
         * @param checksumPath
         * @throws AnnotatorException
         */
        private void generateAndWriteChecksum(File sourceCodeDir, Path checksumPath, HashType hashType) throws AnnotatorException {
                try {
                        final HashProvider hash = new HashProviderFactory().getProvider(hashType);
                        Optional<Exception> exception = Files.find(
                                Paths.get(sourceCodeDir.toURI()), 
                                Integer.MAX_VALUE, 
                                (Path filePath, BasicFileAttributes fileAttr) -> !fileAttr.isDirectory()) 
                                .sorted() 
                                .flatMap((Path f) ->  {
                                        try {
                                                hash.update(Files.readAllBytes(f));
                                                return null;
                                        } catch (Exception e) {
                                                return Stream.of(e);
                                        }
                                })
                                .reduce((Exception exc1, Exception exc2) -> {
                                        exc1.addSuppressed(exc2);
                                        return exc1;
                                });
                        if (exception.isPresent()) {
                                throw new AnnotatorException("Error reading files", exception.get());
                        }
                        Files.write(checksumPath, hash.getValue().getBytes());
                } catch (Exception e) {
                        throw new AnnotatorException("foo", e);
                }
        }

}
