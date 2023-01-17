package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.FunctionalityCategoryDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.FunctionalityCategory}.
 */
public interface FunctionalityCategoryService {
    /**
     * Save a functionalityCategory.
     *
     * @param functionalityCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    FunctionalityCategoryDTO save(FunctionalityCategoryDTO functionalityCategoryDTO);

    /**
     * Updates a functionalityCategory.
     *
     * @param functionalityCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    FunctionalityCategoryDTO update(FunctionalityCategoryDTO functionalityCategoryDTO);

    /**
     * Partially updates a functionalityCategory.
     *
     * @param functionalityCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FunctionalityCategoryDTO> partialUpdate(FunctionalityCategoryDTO functionalityCategoryDTO);

    /**
     * Get all the functionalityCategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FunctionalityCategoryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" functionalityCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FunctionalityCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" functionalityCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the functionalityCategory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FunctionalityCategoryDTO> search(String query, Pageable pageable);
}
