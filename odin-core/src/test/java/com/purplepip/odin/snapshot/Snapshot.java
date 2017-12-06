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

package com.purplepip.odin.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.common.OdinRuntimeException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Snapshot {
  private static final String UPDATE_SNAPSHOT_PROPERTY_NAME = "updateSnapshot";
  private static final boolean UPDATE_SNAPSHOT =
      "true".equals(System.getProperty(UPDATE_SNAPSHOT_PROPERTY_NAME));

  private static final Pattern lastSlash = Pattern.compile("(?:/)?[^/]*$");
  private Class<?> clazz;
  private Path path;
  /*
   * Snapshot could be written to by multiple threads so we need to guarantee thread safety
   * with a synchronised list.
   */
  private List<String> lines = Collections.synchronizedList(new ArrayList<>());

  public Snapshot(Class<?> clazz) {
    this.clazz = clazz;
    initialise();
  }

  private void initialise() {
    Path containerPath;
    try {
      containerPath = Paths.get(
          getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      throw new OdinRuntimeException("Cannot initialise path", e);
    }
    LOG.debug("container path : {}", containerPath);
    if (!containerPath.toString().endsWith("target/test-classes")) {
      throw new OdinRuntimeException("Container path is not expected : " + containerPath);
    }
    Path modulePath = containerPath.getParent().getParent();
    LOG.debug("module path : {}", modulePath);
    Path classSourcePath = Paths
        .get(modulePath + "/test/resources/"
        + clazz.getName().replace('.', '/'));
    LOG.debug("class source path : {}", classSourcePath);
    Path snapshotDirectoryPath = Paths.get(
        lastSlash.matcher(classSourcePath.toString()).replaceFirst("/snapshot"));

    LOG.debug("snapshot directory path : {}", snapshotDirectoryPath);
    path = Paths.get(
        snapshotDirectoryPath.toString() + "/" + clazz.getSimpleName() + ".snap");
    LOG.debug("snapshot source path : {}", path);
    lines.add("SNAPSHOT : " + clazz.getName());
  }

  /**
   * Commit snapshot.
   *
   * @throws IOException exception
   */
  private void commit() throws IOException {
    File file = new File(path.toUri());
    if (!file.exists()) {
      File parent = file.getParentFile();
      if (!parent.exists()) {
        if (!parent.mkdirs()) {
          throw new OdinRuntimeException(
              "Cannot create snapshot parent directory : " + parent);
        }
        LOG.info("Creating directory : {}", parent);
      }
      LOG.info("Creating snapshot file : {}", file);
      Files.write(path, lines);
    } else if (UPDATE_SNAPSHOT) {
      LOG.info("Updating snapshot file : {}", file);
      Files.write(path, lines);
    }
  }

  /**
   * Expect snapshot to match snapshot file.
   */
  public void expectMatch() {
    try {
      commit();
    } catch (IOException e) {
      LOG.error("Cannot commit snapshot", e);
    }
    assertThat(path).hasContent(lines.stream().collect(Collectors.joining("\n")));
  }

  public Path getPath() {
    return path;
  }

  public void writeLine(String s) {
    LOG.debug("Writing to snapshot : {}", s);
    lines.add(s);
  }
}
