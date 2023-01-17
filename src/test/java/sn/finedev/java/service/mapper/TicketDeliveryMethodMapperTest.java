package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketDeliveryMethodMapperTest {

    private TicketDeliveryMethodMapper ticketDeliveryMethodMapper;

    @BeforeEach
    public void setUp() {
        ticketDeliveryMethodMapper = new TicketDeliveryMethodMapperImpl();
    }
}
