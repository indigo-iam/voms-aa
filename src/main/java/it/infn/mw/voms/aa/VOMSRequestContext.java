/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.voms.aa;

import it.infn.mw.iam.persistence.model.IamAccount;

public interface VOMSRequestContext {

  public boolean isHandled();

  public void setHandled(boolean complete);

  public IamAccount getIamAccount();

  public void setIamAccount(IamAccount account);

  public VOMSRequest getRequest();

  public VOMSResponse getResponse();

  public String getVOName();

  public void setVOName(String vo);

  public String getHost();

  public void setHost(String hostname);

  public int getPort();

  public void setPort(int port);

  public String getUserAgent();

  public void setUserAgent(String userAgent);
}
