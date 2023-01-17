package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TicketDeliveryMethodTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketDeliveryMethod.class);
        TicketDeliveryMethod ticketDeliveryMethod1 = new TicketDeliveryMethod();
        ticketDeliveryMethod1.setId(1L);
        TicketDeliveryMethod ticketDeliveryMethod2 = new TicketDeliveryMethod();
        ticketDeliveryMethod2.setId(ticketDeliveryMethod1.getId());
        assertThat(ticketDeliveryMethod1).isEqualTo(ticketDeliveryMethod2);
        ticketDeliveryMethod2.setId(2L);
        assertThat(ticketDeliveryMethod1).isNotEqualTo(ticketDeliveryMethod2);
        ticketDeliveryMethod1.setId(null);
        assertThat(ticketDeliveryMethod1).isNotEqualTo(ticketDeliveryMethod2);
    }
}
