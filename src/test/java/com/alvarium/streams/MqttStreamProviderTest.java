package com.alvarium.streams;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alvarium.PublishWrapper;
import com.alvarium.SdkAction;

import org.junit.Test;

/**
 * Tests for connecting with the MQTT broker are not annotated with `@Test` so these tests do not 
 * run during unit testing. This is because no MQTT endpoint is guranteed in the build pipeline for
 * the SDK.
 * 
 * This class does include tests for loading MQTT configuration
 */
public class MqttStreamProviderTest {
  
  // Test that the config-loaded configuration can actually be casted to MqttConfig type
  @Test
  public void mqttShouldLoadConfig() throws StreamException {
    final String path = "./src/test/java/com/alvarium/streams/mqtt-config.json";
    final String testJson;
    try {
      testJson  = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    } catch(IOException e) {
      throw new StreamException("Could not read configuration file", e);
    }
    final StreamInfo info = StreamInfo.fromJson(testJson);
    final MqttConfig config = MqttConfig.class.cast(info.getConfig());
    assertNotNull(config);
  }

  public void mqttShouldPublish() throws Exception {
    String path = "./src/test/java/com/alvarium/streams/mqtt-config.json";
    String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    StreamInfo info = StreamInfo.fromJson(testJson);
    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider mqttProvider =  factory.getProvider(info);
    String msg = "hello";
    PublishWrapper wrapper = new PublishWrapper(SdkAction.CREATE, String.class.toString(), msg);
    mqttProvider.publish(wrapper);
  }

  public void mqttShouldConnect() throws Exception {
    String path = "./src/test/java/com/alvarium/streams/mqtt-config.json";
    String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    StreamInfo info = StreamInfo.fromJson(testJson);
    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider mqttProvider =  factory.getProvider(info);
    mqttProvider.connect();
  }

  public void mqttShouldClose() throws Exception {
    String path = "./src/test/java/com/alvarium/streams/mqtt-config.json";
    String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    StreamInfo info = StreamInfo.fromJson(testJson);
    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider mqttProvider =  factory.getProvider(info);
    mqttProvider.connect();
    String msg = "hello";
    PublishWrapper wrapper = new PublishWrapper(SdkAction.CREATE, String.class.toString(), msg);
    mqttProvider.publish(wrapper);
    mqttProvider.close();
  }
}
