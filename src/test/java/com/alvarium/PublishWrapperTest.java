package com.alvarium;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class PublishWrapperTest {

  @Test
  public void toJsonShouldReturnAppropriateRepresentation() {
    final SdkAction action = SdkAction.CREATE;
    final String messageType = "test type";
    final ArrayList<Integer> content = new ArrayList<Integer>(Arrays.asList(1,2,3,4));     

    final PublishWrapper wrapper = new PublishWrapper(action,messageType, content);
    System.out.println(wrapper.toJson());
  }  

  @Test
  public void fromJsonShouldReturnAppropriateObject() throws IOException {
    String path = "./src/test/java/com/alvarium/publishWrapperData.json";
    final String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);

    final PublishWrapper wrapper = PublishWrapper.fromJson(testJson);
    assertEquals(SdkAction.CREATE, wrapper.getAction());
    assertEquals("test type", wrapper.getMessageType()); 
    assertEquals("test content", wrapper.getContent());
   }
}
