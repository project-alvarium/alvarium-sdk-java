
/*******************************************************************************
 * Copyright 2023 Dell Inc.
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
package com.alvarium.hash;

class NoneProvider implements HashProvider {
  private byte[] hashValue;

  @Override
  public String derive(byte[] data) {
    this.hashValue = data;
    return new String(data);
  }

  @Override
  public void update(byte[] data) {
    this.hashValue = data;
  }

  @Override
  public String getValue() {
    return new String(this.hashValue);
  }
}
