package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.InsuranceAndMicroCreditsContribution;

/**
 * Spring Data JPA repository for the InsuranceAndMicroCreditsContribution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InsuranceAndMicroCreditsContributionRepository
    extends JpaRepository<InsuranceAndMicroCreditsContribution, Long>, JpaSpecificationExecutor<InsuranceAndMicroCreditsContribution> {}
