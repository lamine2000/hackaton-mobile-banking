package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsContributionDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.InsuranceAndMicroCreditsContribution}.
 */
public interface InsuranceAndMicroCreditsContributionService {
    /**
     * Save a insuranceAndMicroCreditsContribution.
     *
     * @param insuranceAndMicroCreditsContributionDTO the entity to save.
     * @return the persisted entity.
     */
    InsuranceAndMicroCreditsContributionDTO save(InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO);

    /**
     * Updates a insuranceAndMicroCreditsContribution.
     *
     * @param insuranceAndMicroCreditsContributionDTO the entity to update.
     * @return the persisted entity.
     */
    InsuranceAndMicroCreditsContributionDTO update(InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO);

    /**
     * Partially updates a insuranceAndMicroCreditsContribution.
     *
     * @param insuranceAndMicroCreditsContributionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InsuranceAndMicroCreditsContributionDTO> partialUpdate(
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO
    );

    /**
     * Get all the insuranceAndMicroCreditsContributions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InsuranceAndMicroCreditsContributionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" insuranceAndMicroCreditsContribution.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InsuranceAndMicroCreditsContributionDTO> findOne(Long id);

    /**
     * Delete the "id" insuranceAndMicroCreditsContribution.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the insuranceAndMicroCreditsContribution corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InsuranceAndMicroCreditsContributionDTO> search(String query, Pageable pageable);
}
