package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.InsuranceAndMicroCreditsActor;

/**
 * Spring Data JPA repository for the InsuranceAndMicroCreditsActor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InsuranceAndMicroCreditsActorRepository
    extends JpaRepository<InsuranceAndMicroCreditsActor, Long>, JpaSpecificationExecutor<InsuranceAndMicroCreditsActor> {}
