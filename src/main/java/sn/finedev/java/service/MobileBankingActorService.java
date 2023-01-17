package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.MobileBankingActorDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.MobileBankingActor}.
 */
public interface MobileBankingActorService {
    /**
     * Save a mobileBankingActor.
     *
     * @param mobileBankingActorDTO the entity to save.
     * @return the persisted entity.
     */
    MobileBankingActorDTO save(MobileBankingActorDTO mobileBankingActorDTO);

    /**
     * Updates a mobileBankingActor.
     *
     * @param mobileBankingActorDTO the entity to update.
     * @return the persisted entity.
     */
    MobileBankingActorDTO update(MobileBankingActorDTO mobileBankingActorDTO);

    /**
     * Partially updates a mobileBankingActor.
     *
     * @param mobileBankingActorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MobileBankingActorDTO> partialUpdate(MobileBankingActorDTO mobileBankingActorDTO);

    /**
     * Get all the mobileBankingActors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MobileBankingActorDTO> findAll(Pageable pageable);

    /**
     * Get all the mobileBankingActors with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MobileBankingActorDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" mobileBankingActor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MobileBankingActorDTO> findOne(Long id);

    /**
     * Delete the "id" mobileBankingActor.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the mobileBankingActor corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MobileBankingActorDTO> search(String query, Pageable pageable);
}
