package com.purplepip.odin.server.rest;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Temporary class to test Spring Data Rest
 */
@Data
@Entity
public class Pattern {
  private @Id @GeneratedValue Long id;
  private int patternAsInt;
  private Pattern() {
  }

  public Pattern(int patternAsInt) {
    this.patternAsInt = patternAsInt;
  }
}
