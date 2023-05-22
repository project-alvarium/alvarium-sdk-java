package com.alvarium.annotators;

import java.util.Map;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

class GitAnnotator extends AbstractAnnotator implements Annotator {

    private final HashType hash;
    private final AnnotationType kind;
    private final SignatureInfo signature;

    protected GitAnnotator(HashType hash, SignatureInfo signature) {
        this.hash = hash;
        this.kind = AnnotationType.GIT;
        this.signature = signature;
    }

    // File (git working directory) is to be passed in the ctx bag
    // expects commitHash and directory from ctx
    @Override
    public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
        final String key = super.deriveHash(hash, data);
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new AnnotatorException("Cannot get host name.", e);
        }

        // check if git commit hash is equal to the provided data
        String commitHash;
        final Map<String, Object> annotatorProperties = ctx.getProperty(
            AnnotationType.GIT.name(),
            Map.class
        );

        try {
            final File gitDirectory = (File) annotatorProperties.get("directory");
            commitHash = getGitCommitHash(gitDirectory);
        } catch (IOException e) {
            throw new AnnotatorException("could not run git command", e);
        }
        final String incomingCommitHash = (String) annotatorProperties.get("commitHash");
        final Boolean isSatisfied = commitHash.contentEquals(incomingCommitHash);

        final Annotation annotation = new Annotation(
                key,
                hash,
                host,
                kind,
                null,
                isSatisfied,
                Instant.now()
        );

        final String annotationSignature = super.signAnnotation(signature.getPrivateKey(), annotation);
        annotation.setSignature(annotationSignature);
        return annotation;
    }

    private String getGitCommitHash(File directory) throws IOException {
        StringBuilder output = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder("git", "rev-parse", "HEAD");
        Process p = processBuilder.directory(directory).start();
        BufferedReader stream = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        while ((line = stream.readLine()) != null) {
            output.append(line);
        }

        return output.toString();
    }
}
