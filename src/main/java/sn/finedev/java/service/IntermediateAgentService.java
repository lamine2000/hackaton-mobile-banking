package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.IntermediateAgentDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.IntermediateAgent}.
 */
public interface IntermediateAgentService {
    /**
     * Save a intermediateAgent.
     *
     * @param intermediateAgentDTO the entity to save.
     * @return the persisted entity.
     */
    IntermediateAgentDTO save(IntermediateAgentDTO intermediateAgentDTO);

    /**
     * Updates a intermediateAgent.
     *
     * @param intermediateAgentDTO the entity to update.
     * @return the persisted entity.
     */
    IntermediateAgentDTO update(IntermediateAgentDTO intermediateAgentDTO);

    /**
     * Partially updates a intermediateAgent.
     *
     * @param intermediateAgentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<IntermediateAgentDTO> partialUpdate(IntermediateAgentDTO intermediateAgentDTO);

    /**
     * Get all the intermediateAgents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<IntermediateAgentDTO> findAll(Pageable pageable);

    /**
     * Get all the intermediateAgents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<IntermediateAgentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" intermediateAgent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<IntermediateAgentDTO> findOne(Long id);

    /**
     * Delete the "id" intermediateAgent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the intermediateAgent corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<IntermediateAgentDTO> search(String query, Pageable pageable);
}
