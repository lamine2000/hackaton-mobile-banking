package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.TicketDelivery;
import sn.finedev.java.domain.TicketDeliveryMethod;
import sn.finedev.java.service.dto.TicketDeliveryDTO;
import sn.finedev.java.service.dto.TicketDeliveryMethodDTO;

/**
 * Mapper for the entity {@link TicketDelivery} and its DTO {@link TicketDeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketDeliveryMapper extends EntityMapper<TicketDeliveryDTO, TicketDelivery> {
    @Mapping(target = "ticketDeliveryMethod", source = "ticketDeliveryMethod", qualifiedByName = "ticketDeliveryMethodId")
    TicketDeliveryDTO toDto(TicketDelivery s);

    @Named("ticketDeliveryMethodId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TicketDeliveryMethodDTO toDtoTicketDeliveryMethodId(TicketDeliveryMethod ticketDeliveryMethod);
}
