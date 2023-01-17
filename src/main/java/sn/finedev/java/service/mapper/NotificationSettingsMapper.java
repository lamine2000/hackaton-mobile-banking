package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.NotificationSettings;
import sn.finedev.java.service.dto.NotificationSettingsDTO;

/**
 * Mapper for the entity {@link NotificationSettings} and its DTO {@link NotificationSettingsDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationSettingsMapper extends EntityMapper<NotificationSettingsDTO, NotificationSettings> {}
