/*******************************************************************************
* Copyright 2024 Dell Inc.
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
 * The @Test annotation is removed from some of the methods in this class as they are considered
 * integration tests rather than unit tests. This lives here for local testing purposes only.
 *
 * To be able to run the tests, you'll have to setup a local Pravega controller on your machine.
 * more info on deployment can be found here:
 * https://github.com/pravega/pravega/blob/master/documentation/src/docs/deployment/run-local.md
 *
 * To be able to read from the stream, you'll have to use the pravega cli tool to subscribe
 * to a specific stream.
 * more info here:
 * https://pravega.io/docs/nightly/getting-started/quick-start/
 */
public class PravegaStreamProviderTest {

  @Test
  public void pravegaConfigShouldLoadFromJson() throws IOException {
    final String path = "./src/test/java/com/alvarium/streams/pravega-config.json";
    final String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    final StreamInfo streamInfo = StreamInfo.fromJson(testJson);
    final PravegaConfig config = PravegaConfig.class.cast(streamInfo.getConfig());

    assertNotNull(config);
  }

  public void pravegaStreamProviderShouldConnect() throws StreamException, IOException {
    final String path = "./src/test/java/com/alvarium/streams/pravega-config.json";
    final String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    final StreamInfo info = StreamInfo.fromJson(testJson);

    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider pravega = factory.getProvider(info);
    pravega.connect();
    pravega.close();
  }

  public void pravegaStreamProviderShouldPublish() throws StreamException, IOException {
    final String path = "./src/test/java/com/alvarium/streams/pravega-config.json";
    final String testJson = Files.readString(Paths.get(path), StandardCharsets.US_ASCII);
    final StreamInfo info = StreamInfo.fromJson(testJson);

    StreamProviderFactory factory = new StreamProviderFactory();
    StreamProvider pravega = factory.getProvider(info);
    pravega.connect();

    PublishWrapper wrapper =
        new PublishWrapper(SdkAction.CREATE, String.class.getSimpleName(), "test");
    pravega.publish(wrapper);

    pravega.close();
  }
}
