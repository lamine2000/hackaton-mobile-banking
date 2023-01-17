package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.TicketDeliveryMethodDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.TicketDeliveryMethod}.
 */
public interface TicketDeliveryMethodService {
    /**
     * Save a ticketDeliveryMethod.
     *
     * @param ticketDeliveryMethodDTO the entity to save.
     * @return the persisted entity.
     */
    TicketDeliveryMethodDTO save(TicketDeliveryMethodDTO ticketDeliveryMethodDTO);

    /**
     * Updates a ticketDeliveryMethod.
     *
     * @param ticketDeliveryMethodDTO the entity to update.
     * @return the persisted entity.
     */
    TicketDeliveryMethodDTO update(TicketDeliveryMethodDTO ticketDeliveryMethodDTO);

    /**
     * Partially updates a ticketDeliveryMethod.
     *
     * @param ticketDeliveryMethodDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TicketDeliveryMethodDTO> partialUpdate(TicketDeliveryMethodDTO ticketDeliveryMethodDTO);

    /**
     * Get all the ticketDeliveryMethods.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketDeliveryMethodDTO> findAll(Pageable pageable);

    /**
     * Get the "id" ticketDeliveryMethod.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TicketDeliveryMethodDTO> findOne(Long id);

    /**
     * Delete the "id" ticketDeliveryMethod.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the ticketDeliveryMethod corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketDeliveryMethodDTO> search(String query, Pageable pageable);
}
