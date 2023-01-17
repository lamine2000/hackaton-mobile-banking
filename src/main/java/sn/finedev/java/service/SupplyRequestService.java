package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.SupplyRequestDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.SupplyRequest}.
 */
public interface SupplyRequestService {
    /**
     * Save a supplyRequest.
     *
     * @param supplyRequestDTO the entity to save.
     * @return the persisted entity.
     */
    SupplyRequestDTO save(SupplyRequestDTO supplyRequestDTO);

    /**
     * Updates a supplyRequest.
     *
     * @param supplyRequestDTO the entity to update.
     * @return the persisted entity.
     */
    SupplyRequestDTO update(SupplyRequestDTO supplyRequestDTO);

    /**
     * Partially updates a supplyRequest.
     *
     * @param supplyRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SupplyRequestDTO> partialUpdate(SupplyRequestDTO supplyRequestDTO);

    /**
     * Get all the supplyRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SupplyRequestDTO> findAll(Pageable pageable);

    /**
     * Get the "id" supplyRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SupplyRequestDTO> findOne(Long id);

    /**
     * Delete the "id" supplyRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the supplyRequest corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SupplyRequestDTO> search(String query, Pageable pageable);
}
