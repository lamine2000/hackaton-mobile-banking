package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Notification;
import sn.finedev.java.service.dto.NotificationDTO;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {}
