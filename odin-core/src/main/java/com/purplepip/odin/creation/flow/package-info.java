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

/**
 * Sequence flow logic foundations.  The flow implementation acts a layer over and above the
 * sequence implementation.  Sequence plugins creation needs to be easy and light weight.  The
 * flow layer allows common logic to be centrally implemented removing overhead from sequence
 * plugin creation.
 */
package com.purplepip.odin.creation.flow;
