package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * Persistable project.
 */
@Data
@Entity(name = "Project")
@Table(name = "Project")
public class PersistableProject implements Project {
  @Id
  @GeneratedValue
  private Long id;
  private String name;
  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, targetEntity = PersistableSequence.class)
  private Set<Sequence> sequences = new HashSet<>();
  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, targetEntity = PersistableChannel.class)
  private Set<Channel> channels = new HashSet<>();
}
