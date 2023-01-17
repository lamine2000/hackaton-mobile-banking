package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsActorDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.InsuranceAndMicroCreditsActor}.
 */
public interface InsuranceAndMicroCreditsActorService {
    /**
     * Save a insuranceAndMicroCreditsActor.
     *
     * @param insuranceAndMicroCreditsActorDTO the entity to save.
     * @return the persisted entity.
     */
    InsuranceAndMicroCreditsActorDTO save(InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO);

    /**
     * Updates a insuranceAndMicroCreditsActor.
     *
     * @param insuranceAndMicroCreditsActorDTO the entity to update.
     * @return the persisted entity.
     */
    InsuranceAndMicroCreditsActorDTO update(InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO);

    /**
     * Partially updates a insuranceAndMicroCreditsActor.
     *
     * @param insuranceAndMicroCreditsActorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InsuranceAndMicroCreditsActorDTO> partialUpdate(InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO);

    /**
     * Get all the insuranceAndMicroCreditsActors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InsuranceAndMicroCreditsActorDTO> findAll(Pageable pageable);

    /**
     * Get the "id" insuranceAndMicroCreditsActor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InsuranceAndMicroCreditsActorDTO> findOne(Long id);

    /**
     * Delete the "id" insuranceAndMicroCreditsActor.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the insuranceAndMicroCreditsActor corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InsuranceAndMicroCreditsActorDTO> search(String query, Pageable pageable);
}
