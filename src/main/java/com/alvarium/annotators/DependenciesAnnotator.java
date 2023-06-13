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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Map;

import com.alvarium.annotators.dependencies.PackageFileHandler;
import com.alvarium.annotators.dependencies.PackageFileHandlerFactory;
import com.alvarium.annotators.dependencies.DependenciesException;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;



public class DependenciesAnnotator extends AbstractAnnotator implements Annotator {
    final HashType hash;
    final SignatureInfo sign;
    final AnnotationType kind;

    protected DependenciesAnnotator(HashType hash, SignatureInfo signature) {
        this.hash = hash;
        this.sign = signature;
        this.kind = AnnotationType.DEPENDENCIES;
    }

    @Override
    public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
        final String key = super.deriveHash(hash, data);
    
        String host;
        try {
          host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
          throw new AnnotatorException("Cannot get host name", e);
        }  

        String dir = ctx.getProperty(AnnotationType.DEPENDENCIES.name(), String.class);
        Map<String, String> packages = getPackages(dir);
        

        final boolean isSatisfied = false; // TODO (Omar Eissa): Add satisfaction logic

        final Annotation annotation = new Annotation(
            key, 
            this.hash, 
            host, 
            this.kind, 
            null, 
            isSatisfied, 
            Instant.now()
        );

        final String annotationSignature = super.signAnnotation(
            this.sign.getPrivateKey(), 
            annotation
        );

        annotation.setSignature(annotationSignature);
        return annotation;
    }
    
    private Map<String, String> getPackages(String dir) throws AnnotatorException {
        try {
            final PackageFileHandler handler = new PackageFileHandlerFactory().getHandler(dir);
            return handler.getPackages();
        } catch (DependenciesException e) {
            throw new AnnotatorException("Failed to get dependency info", e);
        } catch (Exception e) {
            throw new AnnotatorException("Failed to get dependency info", e);
        }
    }   

}

