package com.alvarium.streams;

import com.alvarium.PublishWrapper;
import com.alvarium.SdkAction;

import org.junit.Test;

public class StreamProviderTest {
  @Test
  public void mockStreamProviderShouldNotThrow() throws StreamException {
    final StreamProviderFactory factory = new StreamProviderFactory();
    final StreamProvider provider = factory.getProvider(StreamType.MOCK);
    final PublishWrapper wrapper = new PublishWrapper(SdkAction.CREATE, "test type",
        "test content");


    provider.connect();
    provider.publish(wrapper);
    provider.close();
  }  
}
