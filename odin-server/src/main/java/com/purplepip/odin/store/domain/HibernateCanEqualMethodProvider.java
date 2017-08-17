/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.store.domain;

import org.hibernate.proxy.HibernateProxyHelper;

/**
 * Don't we love how complex equality can be?  Hibernate entities might have come via a proxy
 * so we can't rely on plain instanceof checks.  Lombok so kindly exposes the canEquals method
 * so we can inject our view of equality - see https://projectlombok.org/features/EqualsAndHashCode
 * - and this class provides the implementation we want to inject across our persistent beans.
 */
final class HibernateCanEqualMethodProvider {
  static boolean canEqualHibernateEntity(Object that, Object other) {
    Class<?> objClass = HibernateProxyHelper.getClassWithoutInitializingProxy(other);
    return that.getClass() == objClass;
  }

  private HibernateCanEqualMethodProvider() {
  }
}
