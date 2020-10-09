/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2020
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
package it.infn.mw.voms.aa.impl;


import org.italiangrid.voms.VOMSAttribute;

import it.infn.mw.voms.aa.VOMSRequestContext;

public class LimitToOwnedFQANsPolicy implements FQANFilteringPolicy {

  public LimitToOwnedFQANsPolicy() {
    // empty ctor
  }

  @Override
  public boolean filterIssuedFQANs(VOMSRequestContext context) {

    if (context.getRequest().getRequestAttributes().isEmpty()) {
      return false;
    }

    boolean modified = false;

    for (VOMSAttribute a : context.getRequest().getRequestAttributes()) {

      // Only consider valid VOMS attributes for this VO
      if (!a.getVO().equals(context.getVOName()))
        continue;

      // Limit issued FQANs to the owned attributes
      modified = context.getResponse().getIssuedFQANs().retainAll(a.getFQANs());
    }

    return modified;
  }

}
