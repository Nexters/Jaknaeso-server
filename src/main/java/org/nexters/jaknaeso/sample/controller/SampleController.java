package org.nexters.jaknaeso.sample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/samples")
@RestController
public class SampleController {

  @GetMapping("{sampleId}")
  ResponseEntity<SampleResponse> getSampleById(@PathVariable final Long sampleId) {
    return ResponseEntity.ok(new SampleResponse(sampleId));
  }

  public record SampleResponse(
      Long sampleId
  ) {

  }
}
