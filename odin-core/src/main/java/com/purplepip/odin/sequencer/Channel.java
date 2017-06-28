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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.project.Project;

/**
 * Channel configuration.
 */
public interface Channel {
  /**
   * Unique channel ID.
   *
   * @return channel ID
   */
  long getId();

  /**
   * Get channel number.
   *
   * @return channel number
   */
  int getNumber();

  /**
   * Set channel number.
   *
   * @param number channel number
   */
  void setNumber(int number);

  /**
   * Get program name.
   *
   * @return program name
   */
  String getProgramName();

  /**
   * Set program name.
   *
   * @param programName program name
   */
  void setProgramName(String programName);

  /**
   * Get program number.
   *
   * @return program number
   */
  int getProgram();

  /**
   * Set program name.
   *
   * @param program program name
   */
  void setProgram(int program);

  /**
   * Get the project.
   *
   * @return project
   */
  Project getProject();

  /**
   * Set the project.
   *
   * @param project project
   */
  void setProject(Project project);
}
