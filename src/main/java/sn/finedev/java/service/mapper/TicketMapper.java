package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Event;
import sn.finedev.java.domain.Payment;
import sn.finedev.java.domain.Ticket;
import sn.finedev.java.domain.TicketDelivery;
import sn.finedev.java.service.dto.EventDTO;
import sn.finedev.java.service.dto.PaymentDTO;
import sn.finedev.java.service.dto.TicketDTO;
import sn.finedev.java.service.dto.TicketDeliveryDTO;

/**
 * Mapper for the entity {@link Ticket} and its DTO {@link TicketDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventId")
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    @Mapping(target = "ticketDelivery", source = "ticketDelivery", qualifiedByName = "ticketDeliveryId")
    TicketDTO toDto(Ticket s);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);

    @Named("ticketDeliveryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TicketDeliveryDTO toDtoTicketDeliveryId(TicketDelivery ticketDelivery);
}
