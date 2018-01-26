/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.store;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.store.domain.PersistablePerformance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersistablePerformanceContainer extends PerformanceContainer {
  @Override
  public void setPerformance(Performance performance) {
    if (!(performance instanceof PersistablePerformance)) {
      throw new OdinRuntimeException(performance + " is not a PersistablePerformance");
    }
    super.setPerformance(performance);
  }
}
