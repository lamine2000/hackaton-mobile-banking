package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketDeliveryMapperTest {

    private TicketDeliveryMapper ticketDeliveryMapper;

    @BeforeEach
    public void setUp() {
        ticketDeliveryMapper = new TicketDeliveryMapperImpl();
    }
}
