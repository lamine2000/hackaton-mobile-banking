package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.Transac;

/**
 * Spring Data JPA repository for the Transac entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransacRepository extends JpaRepository<Transac, Long>, JpaSpecificationExecutor<Transac> {}
