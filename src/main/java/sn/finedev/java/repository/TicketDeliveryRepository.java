package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.TicketDelivery;

/**
 * Spring Data JPA repository for the TicketDelivery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketDeliveryRepository extends JpaRepository<TicketDelivery, Long>, JpaSpecificationExecutor<TicketDelivery> {}
