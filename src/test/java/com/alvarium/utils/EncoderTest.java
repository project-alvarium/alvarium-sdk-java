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
package com.alvarium.utils;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;

import org.junit.Test;

public class EncoderTest {
  private final byte[][] bytes = {
    {(byte) 0xAA, (byte) 0xFF, (byte) 0x1}, {(byte) 0xB, (byte) 0x0, (byte) 0x11, (byte) 0x4F}
  };
  private final String[] hex = {"AAFF01", "0B00114F"};

  @Test
  public void bytesToHexShouldReturnDoubleLength() {
    for (int i = 0; i < 20; i++) {
      byte[] data = new byte[i];

      new SecureRandom().nextBytes(data);
      String hexResult = Encoder.bytesToHex(data);
      assertEquals(data.length * 2, hexResult.length());
    }
  }

  @Test
  public void bytesToHexShouldPass() {
    for (int i = 0; i < bytes.length; i++) {
      final String hexResult = Encoder.bytesToHex(bytes[i]);
      assertEquals(hex[i], hexResult);
    }
  }

  @Test
  public void hexToBytesShouldRemoveIncompleteByte() {
    final String data = "11F";
    final byte[] expectedBytes = {0x11};
    final byte[] bytesResult = Encoder.hexToBytes(data);
    assertEquals(new String(expectedBytes), new String(bytesResult));
  }

  @Test
  public void hexToBytesShouldConvertToByte() {
    final String data = "111F";
    final byte[] expectedBytes = {0x11, 0x1F};
    final byte[] bytesResult = Encoder.hexToBytes(data);
    assertEquals(new String(expectedBytes), new String(bytesResult));
  }
}
