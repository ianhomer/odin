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
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Snapshot {
  private static final String UPDATE_SNAPSHOT_PROPERTY_NAME = "updateSnapshot";
  private static final boolean UPDATE_SNAPSHOT =
      "true".equals(System.getProperty(UPDATE_SNAPSHOT_PROPERTY_NAME));

  private static final Pattern lastSlash = Pattern.compile("(?:/)?[^/]*$");
  private Class<?> clazz;
  private Path path;
  private boolean header = true;
  private String variation = null;
  private String separator = "-";
  private String extension = "snap";
  private String root = "src/test/resources";
  private String relativePath;

  /*
   * Snapshot could be written to by multiple threads so we need to guarantee thread safety
   * with a synchronised list.
   */
  private List<Entry> lines = Collections.synchronizedList(new ArrayList<>());

  /**
   * Create an instance with auto initialisation.
   *
   * @param clazz class to base the snapshot name on
   */
  public Snapshot(Class<?> clazz) {
    this(clazz, false);
  }

  /**
   * Create an instance.
   *
   * @param clazz class to base the snapshot name on
   * @param autoInitialise whether to auto initialise the snapshot
   */
  public Snapshot(Class<?> clazz, boolean autoInitialise) {
    this.clazz = clazz;
    if (autoInitialise) {
      initialise();
    }
  }

  /**
   * Whether to include header in snapshot.
   *
   * @param header whether to include header
   * @return this
   */
  public Snapshot header(boolean header) {
    this.header = header;
    return this;
  }

  /**
   * Variation in snapshot name.
   *
   * @param variation name variation
   * @return this
   */
  public Snapshot variation(String variation) {
    this.variation = variation;
    return this;
  }

  /**
   * Separator between standard name and variation.
   *
   * @param separator separator
   * @return this
   */
  public Snapshot separator(String separator) {
    this.separator = separator;
    return this;
  }

  /**
   * Extension for the snapshot file.
   *
   * @param extension extension
   * @return this
   */
  public Snapshot extension(String extension) {
    this.extension = extension;
    return this;
  }

  /**
   * Root of snapshot files.
   *
   * @param root root of snapshot files relative to current module
   * @return this
   */
  public Snapshot root(String root) {
    this.root = root;
    return this;
  }

  public Snapshot path(String path) {
    this.relativePath = path;
    return this;
  }


  /**
   * Initialise this snapshot.
   *
   * @return this
   */
  public Snapshot initialise() {
    /*
     * Path of the source file for the given class.
     */
    Path containerPath;
    try {
      containerPath = Paths.get(
          clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      throw new OdinRuntimeException("Cannot initialise path", e);
    }
    LOG.debug("container path : {}", containerPath);
    if (!containerPath.toString().endsWith("target/test-classes") &&
        !containerPath.toString().endsWith("target/classes")) {
      throw new OdinRuntimeException("Container path is not expected : " + containerPath);
    }
    /*
     * Path of the maven module that the class belongs to.
     */
    Path modulePath = containerPath.getParent().getParent();
    LOG.debug("module path : {}", modulePath);

    /*
     * Path that snapshots should be stored in.
     */
    Path rootPath = Paths.get(modulePath.toString(), root);
    LOG.debug("root path : {}", rootPath);

    /*
     * Path of the given snapshot without variation or extension parts.
     */
    Path namedPath;
    if (relativePath == null) {
      Path classSourcePath = Paths
          .get(rootPath.toString(), clazz.getName().replace('.', '/'));
      LOG.debug("class source path : {}", classSourcePath);

      Path snapshotDirectoryPath = Paths.get(
          lastSlash.matcher(classSourcePath.toString()).replaceFirst("/snapshot"));

      LOG.debug("snapshot directory path : {}", snapshotDirectoryPath);
      namedPath = Paths.get(
          snapshotDirectoryPath.toString(), clazz.getSimpleName());
    } else {
      namedPath = Paths.get(rootPath.toString(), relativePath);
    }

    path = Paths.get(namedPath
            + (variation == null ? "" : separator + variation)
            + (extension == null ? "" : "." + extension));

    LOG.debug("snapshot source path : {}", path);
    if (header) {
      lines.add(new Entry(-2, "SNAPSHOT : " + clazz.getName()));
    }
    return this;
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
      Files.write(path, linesAsList());
    } else if (UPDATE_SNAPSHOT) {
      LOG.info("Updating snapshot file : {}", file);
      Files.write(path, linesAsList());
    }
  }

  private List<String> linesAsList() {
    return linesAsSortedStream().collect(Collectors.toList());
  }

  private Stream<String> linesAsSortedStream() {
    return lines.stream().sorted(Comparator
        .comparing(Entry::getTime).thenComparing(Entry::getValue)).map(entry -> entry.value);
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
    assertThat(path).hasContent(linesAsSortedStream().collect(Collectors.joining("\n")));
  }

  public Path getPath() {
    return path;
  }

  public void writeLine(String s) {
    writeLine(0, s);
  }

  public void writeLine(long time, String s) {
    LOG.debug("Writing to snapshot {} : {}", time, s);
    lines.add(new Entry(time, s));
  }

  private class Entry {
    private long time;
    private String value;

    public Entry(long time, String value) {
      this.time = time;
      this.value = value;
    }

    public long getTime() {
      return time;
    }

    public String getValue() {
      return value;
    }
  }
}
