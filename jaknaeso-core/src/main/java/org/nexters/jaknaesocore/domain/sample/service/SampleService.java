package org.nexters.jaknaesocore.domain.sample.service;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.sample.model.Sample;
import org.nexters.jaknaesocore.domain.sample.repository.SampleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SampleService {

  private final SampleJpaRepository sampleJpaRepository;

  @Transactional
  public Long save() {
    return sampleJpaRepository.save(Sample.of()).getId();
  }

  @Transactional(readOnly = true)
  public Long getBy(final Long sampleId) {
    return sampleJpaRepository
        .findById(sampleId)
        .orElseThrow(() -> CustomException.SAMPLE_NOT_FOUND)
        .getId();
  }
}
