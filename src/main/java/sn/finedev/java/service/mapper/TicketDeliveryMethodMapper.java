package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.TicketDeliveryMethod;
import sn.finedev.java.service.dto.TicketDeliveryMethodDTO;

/**
 * Mapper for the entity {@link TicketDeliveryMethod} and its DTO {@link TicketDeliveryMethodDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketDeliveryMethodMapper extends EntityMapper<TicketDeliveryMethodDTO, TicketDeliveryMethod> {}
