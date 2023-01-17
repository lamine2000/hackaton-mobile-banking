package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.StoreDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.Store}.
 */
public interface StoreService {
    /**
     * Save a store.
     *
     * @param storeDTO the entity to save.
     * @return the persisted entity.
     */
    StoreDTO save(StoreDTO storeDTO);

    /**
     * Updates a store.
     *
     * @param storeDTO the entity to update.
     * @return the persisted entity.
     */
    StoreDTO update(StoreDTO storeDTO);

    /**
     * Partially updates a store.
     *
     * @param storeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StoreDTO> partialUpdate(StoreDTO storeDTO);

    /**
     * Get all the stores.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StoreDTO> findAll(Pageable pageable);

    /**
     * Get all the stores with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StoreDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" store.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StoreDTO> findOne(Long id);

    /**
     * Delete the "id" store.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the store corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StoreDTO> search(String query, Pageable pageable);
}
