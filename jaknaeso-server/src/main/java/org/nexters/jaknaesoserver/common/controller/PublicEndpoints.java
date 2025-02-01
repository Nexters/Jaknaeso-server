package org.nexters.jaknaesoserver.common.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum PublicEndpoints {
  SAMPLE("/api/samples/**"),
  AUTH("/api/v1/auth/**"),
  ;

  private final String path;

  PublicEndpoints(String path) {
    this.path = path;
  }

  public static List<String> getAllPaths() {
    return Arrays.stream(PublicEndpoints.values())
        .map(PublicEndpoints::getPath)
        .collect(Collectors.toList());
  }

  public static String[] getPatterns() {
    return Arrays.stream(PublicEndpoints.values())
        .map(PublicEndpoints::getPath)
        .toArray(String[]::new);
  }
}
