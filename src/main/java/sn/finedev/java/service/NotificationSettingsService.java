package sn.finedev.java.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.finedev.java.service.dto.NotificationSettingsDTO;

/**
 * Service Interface for managing {@link sn.finedev.java.domain.NotificationSettings}.
 */
public interface NotificationSettingsService {
    /**
     * Save a notificationSettings.
     *
     * @param notificationSettingsDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationSettingsDTO save(NotificationSettingsDTO notificationSettingsDTO);

    /**
     * Updates a notificationSettings.
     *
     * @param notificationSettingsDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationSettingsDTO update(NotificationSettingsDTO notificationSettingsDTO);

    /**
     * Partially updates a notificationSettings.
     *
     * @param notificationSettingsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationSettingsDTO> partialUpdate(NotificationSettingsDTO notificationSettingsDTO);

    /**
     * Get all the notificationSettings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationSettingsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" notificationSettings.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationSettingsDTO> findOne(Long id);

    /**
     * Delete the "id" notificationSettings.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the notificationSettings corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationSettingsDTO> search(String query, Pageable pageable);
}
