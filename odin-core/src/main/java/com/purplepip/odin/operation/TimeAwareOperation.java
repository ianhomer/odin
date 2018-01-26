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

package com.purplepip.odin.operation;

/**
 * A time aware operation is one that is aware of the time it was originally fire.  Note that for
 * transmitted operations we may fire the operation immediately and hence may loose the timing of
 * the original operation.  Implementing this interface allows us to retain the timing of the
 * original event.
 */
public interface TimeAwareOperation extends Operation {
  long getTime();
}
