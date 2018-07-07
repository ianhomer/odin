package com.purplepip.odin.audio;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

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
@Slf4j
public class AudioDeviceTest {
  @Test
  public void testSummary() throws LineUnavailableException {
    Mixer.Info mixerInfo = mock(Mixer.Info.class);
    AudioHandle handle = mock(AudioHandle.class);
    Mixer mixer = mock(Mixer.class);
    Line.Info[] sourceLineInfos = new Line.Info[] {mock(Line.Info.class)};
    when(mixer.getSourceLineInfo()).thenReturn(sourceLineInfos);
    Line.Info[] targetLineInfos = new Line.Info[] {mock(Line.Info.class)};
    when(mixer.getTargetLineInfo()).thenReturn(targetLineInfos);
    when(mixer.getMixerInfo()).thenReturn(mixerInfo);
    Line line = mock(Line.class);
    when(mixer.getLine(any())).thenReturn(line);
    Control control = mock(Control.class);
    when(control.getType()).thenReturn(mock(Type.class));
    Control[] controls = new Control[] {control};
    when(line.getControls()).thenReturn(controls);
    AudioDevice device = new AudioDevice(handle, mixer);
    assertThat(device.getSummary(), containsString("control : null"));
  }
}
