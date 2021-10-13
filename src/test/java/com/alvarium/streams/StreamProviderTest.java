
/*******************************************************************************
 * Copyright 2021 Dell Inc.
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

import com.alvarium.PublishWrapper;
import com.alvarium.SdkAction;

import org.junit.Test;

public class StreamProviderTest {
  @Test
  public void mockStreamProviderShouldNotThrow() throws StreamException {
    final StreamProviderFactory factory = new StreamProviderFactory();
    final StreamInfo info = new StreamInfo(StreamType.MOCK, null);
    final StreamProvider provider = factory.getProvider(info);
    final PublishWrapper wrapper = new PublishWrapper(SdkAction.CREATE, "test type",
        "test content");


    provider.connect();
    provider.publish(wrapper);
    provider.close();
  }  
}
