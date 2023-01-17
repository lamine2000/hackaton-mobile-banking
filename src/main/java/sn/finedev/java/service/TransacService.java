package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.TransacDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.Transac}.
 */
public interface TransacService {
    /**
     * Save a transac.
     *
     * @param transacDTO the entity to save.
     * @return the persisted entity.
     */
    TransacDTO save(TransacDTO transacDTO);

    /**
     * Updates a transac.
     *
     * @param transacDTO the entity to update.
     * @return the persisted entity.
     */
    TransacDTO update(TransacDTO transacDTO);

    /**
     * Partially updates a transac.
     *
     * @param transacDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransacDTO> partialUpdate(TransacDTO transacDTO);

    /**
     * Get all the transacs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransacDTO> findAll(Pageable pageable);

    /**
     * Get the "id" transac.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransacDTO> findOne(Long id);

    /**
     * Delete the "id" transac.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transac corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransacDTO> search(String query, Pageable pageable);
}
