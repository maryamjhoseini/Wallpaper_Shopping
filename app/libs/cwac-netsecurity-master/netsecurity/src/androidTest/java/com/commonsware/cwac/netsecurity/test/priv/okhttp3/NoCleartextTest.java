/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 */

package com.commonsware.cwac.netsecurity.test.priv.okhttp3;

import android.support.test.InstrumentationRegistry;
import com.commonsware.cwac.netsecurity.BuildConfig;
import com.commonsware.cwac.netsecurity.TrustManagerBuilder;
import com.commonsware.cwac.netsecurity.test.AbstractOkHttp3Test;
import com.commonsware.cwac.netsecurity.test.R;

public class NoCleartextTest extends AbstractOkHttp3Test {
  @Override
  protected String getUrl() {
    return(BuildConfig.TEST_PRIVATE_HTTP_URL);
  }

  @Override
  protected TrustManagerBuilder getBuilder() {
    return(new TrustManagerBuilder().withConfig(
      InstrumentationRegistry.getContext(), R.xml.okhttp3_selfsigned_noclear,
      true));
  }

  @Override
  protected boolean isPositiveTest() {
    return(false);
  }
}
