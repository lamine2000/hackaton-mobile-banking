package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.TownDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.Town}.
 */
public interface TownService {
    /**
     * Save a town.
     *
     * @param townDTO the entity to save.
     * @return the persisted entity.
     */
    TownDTO save(TownDTO townDTO);

    /**
     * Updates a town.
     *
     * @param townDTO the entity to update.
     * @return the persisted entity.
     */
    TownDTO update(TownDTO townDTO);

    /**
     * Partially updates a town.
     *
     * @param townDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TownDTO> partialUpdate(TownDTO townDTO);

    /**
     * Get all the towns.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TownDTO> findAll(Pageable pageable);

    /**
     * Get the "id" town.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TownDTO> findOne(Long id);

    /**
     * Delete the "id" town.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the town corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TownDTO> search(String query, Pageable pageable);
}
