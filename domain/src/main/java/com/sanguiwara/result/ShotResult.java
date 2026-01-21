package com.sanguiwara.result;

import java.util.List;

public interface ShotResult<E> {
     int attempts();
     int made();
     List<E> events();

     default double fgPct() {
          return attempts() == 0 ? 0.0 : (made() * 1.0 / attempts());
     }
}
