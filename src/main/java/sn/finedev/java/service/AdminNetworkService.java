package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.AdminNetworkDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.AdminNetwork}.
 */
public interface AdminNetworkService {
    /**
     * Save a adminNetwork.
     *
     * @param adminNetworkDTO the entity to save.
     * @return the persisted entity.
     */
    AdminNetworkDTO save(AdminNetworkDTO adminNetworkDTO);

    /**
     * Updates a adminNetwork.
     *
     * @param adminNetworkDTO the entity to update.
     * @return the persisted entity.
     */
    AdminNetworkDTO update(AdminNetworkDTO adminNetworkDTO);

    /**
     * Partially updates a adminNetwork.
     *
     * @param adminNetworkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AdminNetworkDTO> partialUpdate(AdminNetworkDTO adminNetworkDTO);

    /**
     * Get all the adminNetworks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AdminNetworkDTO> findAll(Pageable pageable);

    /**
     * Get all the adminNetworks with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AdminNetworkDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" adminNetwork.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AdminNetworkDTO> findOne(Long id);

    /**
     * Delete the "id" adminNetwork.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the adminNetwork corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AdminNetworkDTO> search(String query, Pageable pageable);
}
