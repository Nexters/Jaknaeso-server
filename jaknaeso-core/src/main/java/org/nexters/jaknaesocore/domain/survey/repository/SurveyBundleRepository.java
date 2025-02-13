package org.nexters.jaknaesocore.domain.survey.repository;

import java.util.Collection;
import java.util.Optional;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyBundleRepository extends JpaRepository<SurveyBundle, Long> {

  Optional<SurveyBundle> findFirstByIdNotInOrderByIdAsc(Collection<Long> ids);
}
