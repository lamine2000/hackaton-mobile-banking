package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.FinalAgentDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.FinalAgent}.
 */
public interface FinalAgentService {
    /**
     * Save a finalAgent.
     *
     * @param finalAgentDTO the entity to save.
     * @return the persisted entity.
     */
    FinalAgentDTO save(FinalAgentDTO finalAgentDTO);

    /**
     * Updates a finalAgent.
     *
     * @param finalAgentDTO the entity to update.
     * @return the persisted entity.
     */
    FinalAgentDTO update(FinalAgentDTO finalAgentDTO);

    /**
     * Partially updates a finalAgent.
     *
     * @param finalAgentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FinalAgentDTO> partialUpdate(FinalAgentDTO finalAgentDTO);

    /**
     * Get all the finalAgents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinalAgentDTO> findAll(Pageable pageable);

    /**
     * Get all the finalAgents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinalAgentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" finalAgent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FinalAgentDTO> findOne(Long id);

    /**
     * Delete the "id" finalAgent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the finalAgent corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinalAgentDTO> search(String query, Pageable pageable);
}
