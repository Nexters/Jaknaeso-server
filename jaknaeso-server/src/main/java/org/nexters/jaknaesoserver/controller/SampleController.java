package org.nexters.jaknaesoserver.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.sample.service.SampleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/samples")
@RestController
@RequiredArgsConstructor
public class SampleController {

  private final SampleService sampleService;

  @PostMapping()
  ResponseEntity<SampleResponse> saveSample() {
    return ResponseEntity.ok(new SampleResponse(sampleService.save()));
  }

  @GetMapping("/{sampleId}")
  ResponseEntity<SampleResponse> getSampleById(@PathVariable final Long sampleId) {
    return ResponseEntity.ok(new SampleResponse(sampleService.getBy(sampleId)));
  }

  public record SampleResponse(Long sampleId) {}
}
