package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Event;
import sn.finedev.java.service.dto.EventDTO;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventMapper extends EntityMapper<EventDTO, Event> {}
