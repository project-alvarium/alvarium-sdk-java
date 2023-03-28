package com.alvarium.annotators;


public class SourceCodeAnnotatorProps {
    final private String sourceCodePath;
    final private String checksumPath;

    public SourceCodeAnnotatorProps(String sourceCodePath, String checksumPath) {
        this.checksumPath = checksumPath;
        this.sourceCodePath = sourceCodePath;
    }

    final public String getSourceCodePath() {
        return this.sourceCodePath;
    }
    
    final public String getChecksumPath() {
        return this.checksumPath;
    }
}