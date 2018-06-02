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

package com.purplepip.odin.audio;

import com.purplepip.odin.devices.AbstractDevice;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioDevice extends AbstractDevice {
  private final Mixer mixer;
  private final AudioHandle handle;

  AudioDevice(AudioHandle handle, Mixer mixer) {
    this.mixer = mixer;
    this.handle = handle;
    initialise();
  }

  public AudioHandle getHandle() {
    return handle;
  }

  @Override
  public void close() {
    // No operation necessary for close
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public boolean isSource() {
    return mixer.getSourceLineInfo().length != 0;
  }

  @Override
  public boolean isSink() {
    return mixer.getTargetLineInfo().length != 0;
  }

  public String getName() {
    return mixer.getMixerInfo().getName();
  }

  @Override
  public String getSummary() {
    String newLine = "\n  ";
    StringBuilder sb = new StringBuilder();
    sb.append(mixer.getMixerInfo().getName()).append(" : ")
        .append(mixer.getMixerInfo().getDescription()).append(newLine);
    for (Line.Info lineInfo : mixer.getSourceLineInfo()) {
      try {
        Line line = mixer.getLine(lineInfo);
        try {
          sb.append("source port : ").append(lineInfo).append(newLine);
          if (line instanceof Clip) {
            LOG.debug("Not opening Clip line");
          } else {
            line.open();
            for (Control control : line.getControls()) {
              sb.append("  ").append(new ControlWrapper(control).toString()).append(newLine);
            }
          }
        } catch (IllegalArgumentException e) {
          LOG.info("ERROR {} : Cannot open line {}", e.getMessage(), line.getClass());
          LOG.debug("Cannot open line", e);
        } finally {
          if (line.isOpen()) {
            line.close();
          }
        }
      } catch (LineUnavailableException e) {
        LOG.info("ERROR {} : Cannot get line {}", e.getMessage(), lineInfo);
        LOG.debug("Cannot get line", e);
      }
    }
    return sb.toString();
  }

  @Override
  protected void initialise() {
    setProperty("line.source.count", mixer.getSourceLineInfo().length);
    setProperty("line.target.count", mixer.getTargetLineInfo().length);
  }

  private static final class ControlWrapper {
    private final Control control;
    private final Control.Type type;

    private ControlWrapper(Control control) {
      this.control = control;
      type = control.getType();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("control : ").append(type.toString()).append(' ');
      if (control instanceof BooleanControl) {
        sb.append("(boolean)");
      } else if (control instanceof CompoundControl) {
        for (Control child : ((CompoundControl) control).getMemberControls()) {
          sb.append("child ").append(new ControlWrapper(child).toString());
        }
      } else if (control instanceof EnumControl) {
        sb.append("(enum) : ");
        for (Object value : ((EnumControl) control).getValues()) {
          sb.append(' ').append(value);
        }
      } else if (control instanceof FloatControl) {
        FloatControl floatControl = (FloatControl) control;
        sb.append("(float)")
            .append("max = ").append(floatControl.getMaximum())
            .append("min = ").append(floatControl.getMinimum())
            .append("min = ").append(floatControl.getUnits());
      } else {
        sb.append("unknown : ").append(control.getClass().getName()).append(' ')
            .append(control);
      }
      return sb.toString();
    }
  }
}
