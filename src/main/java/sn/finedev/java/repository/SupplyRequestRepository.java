package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.SupplyRequest;

/**
 * Spring Data JPA repository for the SupplyRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long>, JpaSpecificationExecutor<SupplyRequest> {}
