package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.ZoneDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.Zone}.
 */
public interface ZoneService {
    /**
     * Save a zone.
     *
     * @param zoneDTO the entity to save.
     * @return the persisted entity.
     */
    ZoneDTO save(ZoneDTO zoneDTO);

    /**
     * Updates a zone.
     *
     * @param zoneDTO the entity to update.
     * @return the persisted entity.
     */
    ZoneDTO update(ZoneDTO zoneDTO);

    /**
     * Partially updates a zone.
     *
     * @param zoneDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ZoneDTO> partialUpdate(ZoneDTO zoneDTO);

    /**
     * Get all the zones.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ZoneDTO> findAll(Pageable pageable);

    /**
     * Get the "id" zone.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ZoneDTO> findOne(Long id);

    /**
     * Delete the "id" zone.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the zone corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ZoneDTO> search(String query, Pageable pageable);
}
