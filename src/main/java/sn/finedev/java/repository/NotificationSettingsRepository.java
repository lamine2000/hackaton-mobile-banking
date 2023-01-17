package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.NotificationSettings;

/**
 * Spring Data JPA repository for the NotificationSettings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationSettingsRepository
    extends JpaRepository<NotificationSettings, Long>, JpaSpecificationExecutor<NotificationSettings> {}
