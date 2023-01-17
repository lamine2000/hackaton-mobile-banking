package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.TicketDeliveryDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.TicketDelivery}.
 */
public interface TicketDeliveryService {
    /**
     * Save a ticketDelivery.
     *
     * @param ticketDeliveryDTO the entity to save.
     * @return the persisted entity.
     */
    TicketDeliveryDTO save(TicketDeliveryDTO ticketDeliveryDTO);

    /**
     * Updates a ticketDelivery.
     *
     * @param ticketDeliveryDTO the entity to update.
     * @return the persisted entity.
     */
    TicketDeliveryDTO update(TicketDeliveryDTO ticketDeliveryDTO);

    /**
     * Partially updates a ticketDelivery.
     *
     * @param ticketDeliveryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TicketDeliveryDTO> partialUpdate(TicketDeliveryDTO ticketDeliveryDTO);

    /**
     * Get all the ticketDeliveries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketDeliveryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" ticketDelivery.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TicketDeliveryDTO> findOne(Long id);

    /**
     * Delete the "id" ticketDelivery.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the ticketDelivery corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketDeliveryDTO> search(String query, Pageable pageable);
}
