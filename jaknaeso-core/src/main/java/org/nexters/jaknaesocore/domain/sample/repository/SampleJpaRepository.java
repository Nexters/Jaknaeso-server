package org.nexters.jaknaesocore.domain.sample.repository;

import org.nexters.jaknaesocore.domain.sample.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleJpaRepository extends JpaRepository<Sample, Long> {

}
