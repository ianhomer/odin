package com.purplepip.odin.server;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.MidiSystemWrapper;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.server.rest.PersistableProjectBuilder;
import com.purplepip.odin.server.rest.domain.PersistableProject;
import com.purplepip.odin.server.rest.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring Boot of Odin.
 */
@Configuration
public class OdinConfiguration {
  public static final String DEFAULT_PROJECT_NAME = "defaultProject";

  @Autowired
  private ProjectRepository projectRepository;

  @Bean
  public MidiDeviceWrapper midiDeviceWrapper() {
    return new MidiDeviceWrapper();
  }

  @Bean
  public MeasureProvider measureProvider() {
    return new StaticMeasureProvider(4);
  }

  @Bean
  public MidiSystemWrapper midiSystemWrapper() {
    return new MidiSystemWrapper();
  }

  @Bean
  public ProjectContainer projectContainer() {
    return new ProjectContainer();
  }

  /**
   * Create Odin sequencer.
   *
   * @param measureProvider measure provider
   * @param midiDeviceWrapper MIDI device wrapper
   * @return Odin sequencer
   * @throws OdinException exception
   */
  @Bean
  public OdinSequencer odinSequencer(MeasureProvider measureProvider,
                                     MidiDeviceWrapper midiDeviceWrapper,
                                     ProjectContainer projectContainer) throws OdinException {
    Project project = projectRepository.findByName(DEFAULT_PROJECT_NAME);
    if (project == null) {
      project = new PersistableProject();
      projectContainer.setProject(project);
      new PersistableProjectBuilder(projectContainer)
          .addMetronome()
          .withChannel(1).changeProgramTo("bird")
          .withVelocity(10).withNote(62).addPattern(Ticks.BEAT, 4)
          .withChannel(2).changeProgramTo("aahs")
          .withVelocity(20).withNote(42).addPattern(Ticks.BEAT, 15)
          .withChannel(9).changeProgramTo("TR-909")
          .withVelocity(100).withNote(62).addPattern(Ticks.BEAT, 2)
          .withVelocity(40).addPattern(Ticks.EIGHTH, 127)
          .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);
      projectRepository.save((PersistableProject) project);
    } else {
      projectContainer.setProject(project);
    }
    return new OdinSequencer(
        new DefaultOdinSequencerConfiguration()
            .setProjectContainer(projectContainer)
            .setBeatsPerMinute(new StaticBeatsPerMinute(120))
            .setMeasureProvider(measureProvider)
            .setOperationReceiver(new MidiOperationReceiver(midiDeviceWrapper))
            .setMicrosecondPositionProvider(
                new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));

  }
}
