
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


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashProvider;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

class SourceCodeAnnotator extends AbstractAnnotator implements Annotator {

    private final HashType hash;
    private final AnnotationType kind;
    private final SignatureInfo signature;

    private HashProvider hashProvider;

    protected SourceCodeAnnotator(HashType hash, SignatureInfo signature) {
        this.hash = hash;
        this.kind = AnnotationType.SourceCode;
        this.signature = signature;
    }

    // File (git working directory) is to be passed in the ctx bag
    // expects commitHash and directory from ctx
    @Override
    public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
        this.initHashProvider(this.hash);
        final String key = this.hashProvider.derive(data);
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new AnnotatorException("Cannot get host name.", e);
        }

        final SourceCodeAnnotatorProps props = ctx.getProperty(
            AnnotationType.SourceCode.name(),
            SourceCodeAnnotatorProps.class
        );
        
        final String checksum = this.readChecksum(props.getChecksumPath());
        final String generatedChecksum = this.generateChecksum(props.getSourceCodePath());

        final Boolean isSatisfied = generatedChecksum.equals(checksum);

        final Annotation annotation = new Annotation(
                key,
                hash,
                host,
                kind,
                null,
                isSatisfied,
                Instant.now());

        final String annotationSignature = super.signAnnotation(signature.getPrivateKey(), annotation);
        annotation.setSignature(annotationSignature);
        return annotation;
    }

    private String generateChecksum(String sourceCodePath) throws AnnotatorException {
        final Optional<AnnotatorException> exc = this.findFiles(sourceCodePath)
            .sorted()  // ensures hash is detirministic
            .flatMap(this::readAndHashFile) 
            .reduce(this::exceptionReducer);

            if(exc.isPresent()) {
                final AnnotatorException exception = exc.get();
                throw exception;
            }

            final String hashValue = this.hashProvider.getValue();
            return hashValue;
        
    }

    private String readChecksum(String path) throws AnnotatorException {
        try {
            final Path p = Paths.get(path);
            return Files.readString(p);
        } catch (IOException e) {
            throw new AnnotatorException("Failed to read file, could not validate checksum", e);
        } catch (SecurityException e) {
            throw new AnnotatorException(
                "Insufficient permission to access file, could not validate checksum", 
                e
            );
        } catch (OutOfMemoryError e) {
            throw new AnnotatorException(
                "Failed to read file due to size larger than 2GB, could not validate checksum " + e
            );
        } catch (Exception e) {
            throw new AnnotatorException("Could not validate checksum");
        }
    }

    /**
     *  Initializes the hash provider used to hash the source code 
     * @return HashProvider
     * @throws AnnotatorException - If hashing algorithm not found, 
     * or if an unknown exception was thrown
     */
    private final void initHashProvider(HashType hashType) throws AnnotatorException {
        try {
             this.hashProvider = new HashProviderFactory().getProvider(hashType);
        } catch (HashTypeException e) {
            throw new AnnotatorException("Hashing algorithm not found, could not hash data or generate checksum", e);
        } catch (Exception e) {
            throw new AnnotatorException("Could not hash data or generate checksum", e);
        }
    }

    /**
     * @return Stream of file paths found
     * @throws AnnotatorException - When bad or non-existant Path is provided
     */
    private Stream<Path> findFiles(String directory) throws AnnotatorException {
        try {
            return Files.find(
                Paths.get(directory), 
                Integer.MAX_VALUE, 
                (Path filePath, BasicFileAttributes fileAttr) -> !fileAttr.isDirectory() // read all files
            );
        } catch (IOException e) {
            throw new AnnotatorException("Failed to read files, could not generate checksum", e);
        } catch (Exception e) {
            throw new AnnotatorException("Could not generate checksum", e);
        }
    }

    /**
     * Attempts to read file and update the MD5 hash with it.
     * 
     * @param file - path to file 
     * @return Stream - steam of an exception if any were thrown, if none then returns null
     *  
     */
    private Stream<AnnotatorException> readAndHashFile(Path file) {
        AnnotatorException excWrapper;
        try {
            this.hashProvider.update(Files.readAllBytes(file));
            return null;
        } catch (OutOfMemoryError e) {
            excWrapper = new AnnotatorException(
                "Failed to read file due to size larger than 2GB, could not validate checksum" + e
            );
        } catch (IOException e) {
            excWrapper = new AnnotatorException(
                "Failed to read file contents, could not generate checksum", 
                e
            );
        } catch (SecurityException e) {
            excWrapper = new AnnotatorException(
                "Insufficient permission to access file, could not validate checksum",
                e
            );
        } catch (Exception e) {
            excWrapper = new AnnotatorException("Could not validate checksum", e);
        }
        return Stream.of(excWrapper);
    }

    /**
     * Suppresses all exceptions in favor of the first exception thrown
     * @param accumulator
     * @param exc
     * @return Exception - first exception thrown 
     */
    private AnnotatorException exceptionReducer(AnnotatorException accumulator, AnnotatorException exc) {
        accumulator.addSuppressed(exc);
        return accumulator;
    }


}
