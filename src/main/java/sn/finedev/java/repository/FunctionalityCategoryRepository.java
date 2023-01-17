package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.FunctionalityCategory;

/**
 * Spring Data JPA repository for the FunctionalityCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FunctionalityCategoryRepository
    extends JpaRepository<FunctionalityCategory, Long>, JpaSpecificationExecutor<FunctionalityCategory> {}
