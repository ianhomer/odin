package com.purplepip.odin.demo

import com.purplepip.odin.performance.StaticPerformance

class PlutoPerformance : StaticPerformance({
  mixin(BeatPerformance())
  layer("performance") {
    channel(1, "Strings") {
      play("A/q G/8 A/q E")
    }
  }
})