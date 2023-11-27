package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class Analytics {
  //@Schema(defaultValue = "Number of accepted papers")
  private int accepted;
  private int rejected;
  private int unknown;
}
