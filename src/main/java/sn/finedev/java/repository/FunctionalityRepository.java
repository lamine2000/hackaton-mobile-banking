package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.Functionality;

/**
 * Spring Data JPA repository for the Functionality entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FunctionalityRepository extends JpaRepository<Functionality, Long>, JpaSpecificationExecutor<Functionality> {}
