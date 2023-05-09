package com.alvarium.annotators;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.alvarium.SdkInfo;
import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashInfo;
import com.alvarium.hash.HashType;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;

public class GitAnnotatorTest {

        // test commented out as it is considered an integration test (depends on git)
        // @Test
        public void executeShouldCreateAnnotation() throws AnnotatorException {
                AnnotatorFactory factory = new AnnotatorFactory();
                KeyInfo privateKey = new KeyInfo(
                                "./src/test/java/com/alvarium/annotators/public.key",
                                SignType.Ed25519);
                KeyInfo publicKey = new KeyInfo(
                                "./src/test/java/com/alvarium/annotators/public.key",
                                SignType.Ed25519);

                SignatureInfo sign = new SignatureInfo(publicKey, privateKey);
                final AnnotationType[] annotators = { AnnotationType.GIT };
                final SdkInfo config = new SdkInfo(annotators, new HashInfo(HashType.MD5Hash), sign, null);
                Annotator tpm = factory.getAnnotator(AnnotationType.GIT, config);

                PropertyBag ctx = new ImmutablePropertyBag(
                                Map.of("directory", new File(System.getProperty("user.dir")), "commitHash",
                                                "04bae059747f700858ef46c28abbd7fa6efc7036"));
                byte[] data = "pipeline1/1".getBytes();
                Annotation annotation = tpm.execute(ctx, data);
                System.out.println(annotation.toJson());
        }
}
