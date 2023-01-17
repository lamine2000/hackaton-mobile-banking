package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.TicketDeliveryMethod;

/**
 * Spring Data JPA repository for the TicketDeliveryMethod entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketDeliveryMethodRepository
    extends JpaRepository<TicketDeliveryMethod, Long>, JpaSpecificationExecutor<TicketDeliveryMethod> {}
