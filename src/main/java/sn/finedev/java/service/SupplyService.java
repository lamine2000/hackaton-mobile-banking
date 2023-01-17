package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.SupplyDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.Supply}.
 */
public interface SupplyService {
    /**
     * Save a supply.
     *
     * @param supplyDTO the entity to save.
     * @return the persisted entity.
     */
    SupplyDTO save(SupplyDTO supplyDTO);

    /**
     * Updates a supply.
     *
     * @param supplyDTO the entity to update.
     * @return the persisted entity.
     */
    SupplyDTO update(SupplyDTO supplyDTO);

    /**
     * Partially updates a supply.
     *
     * @param supplyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SupplyDTO> partialUpdate(SupplyDTO supplyDTO);

    /**
     * Get all the supplies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SupplyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" supply.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SupplyDTO> findOne(Long id);

    /**
     * Delete the "id" supply.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the supply corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SupplyDTO> search(String query, Pageable pageable);
}
