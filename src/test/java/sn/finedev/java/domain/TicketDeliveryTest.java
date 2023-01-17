package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TicketDeliveryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketDelivery.class);
        TicketDelivery ticketDelivery1 = new TicketDelivery();
        ticketDelivery1.setId(1L);
        TicketDelivery ticketDelivery2 = new TicketDelivery();
        ticketDelivery2.setId(ticketDelivery1.getId());
        assertThat(ticketDelivery1).isEqualTo(ticketDelivery2);
        ticketDelivery2.setId(2L);
        assertThat(ticketDelivery1).isNotEqualTo(ticketDelivery2);
        ticketDelivery1.setId(null);
        assertThat(ticketDelivery1).isNotEqualTo(ticketDelivery2);
    }
}
