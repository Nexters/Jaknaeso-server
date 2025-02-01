package org.nexters.jaknaesocore.domain.question.repository;

import org.nexters.jaknaesocore.domain.question.model.SurveyBundle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BundleRepository extends JpaRepository<SurveyBundle, Long> {}
