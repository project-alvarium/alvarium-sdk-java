package com.alvarium.streams;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alvarium.PublishWrapper;
import com.alvarium.SdkAction;

import org.junit.Test;

public class MqttStreamProviderTest {
  final StreamInfo info;
  
  public MqttStreamProviderTest() throws Exception {
    String path = "./src/test/java/com/alvarium/streams/mqtt-config.json";
    String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    info = StreamInfo.fromJson(testJson);
  }

  @Test
  public void mqttShouldPublish() throws StreamException {
    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider mqttProvider =  factory.getProvider(info);
    String msg = "hello";
    PublishWrapper wrapper = new PublishWrapper(SdkAction.CREATE, String.class.toString(), msg);
    mqttProvider.publish(wrapper);
  }

  @Test
  public void mqttShouldConnect() throws StreamException {
    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider mqttProvider =  factory.getProvider(info);
    mqttProvider.connect();
  }

  @Test
  public void mqttShouldClose() throws Exception {
    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider mqttProvider =  factory.getProvider(info);
    mqttProvider.connect();
    String msg = "hello";
    PublishWrapper wrapper = new PublishWrapper(SdkAction.CREATE, String.class.toString(), msg);
    mqttProvider.publish(wrapper);
    mqttProvider.close();
  }
}
