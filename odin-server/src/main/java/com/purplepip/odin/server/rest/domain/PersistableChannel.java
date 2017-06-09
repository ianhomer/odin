package com.purplepip.odin.server.rest.domain;

import com.purplepip.odin.sequencer.Channel;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * Persistable channel.
 */
@Data
@Entity(name = "Channel")
@Table(name = "Channel")
public class PersistableChannel implements Channel {
  @Id
  @GeneratedValue
  private Long id;
  private int number;
  private String programName;
  private int program;
}
