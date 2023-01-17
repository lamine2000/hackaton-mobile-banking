package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TicketDeliveryMethodDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketDeliveryMethodDTO.class);
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO1 = new TicketDeliveryMethodDTO();
        ticketDeliveryMethodDTO1.setId(1L);
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO2 = new TicketDeliveryMethodDTO();
        assertThat(ticketDeliveryMethodDTO1).isNotEqualTo(ticketDeliveryMethodDTO2);
        ticketDeliveryMethodDTO2.setId(ticketDeliveryMethodDTO1.getId());
        assertThat(ticketDeliveryMethodDTO1).isEqualTo(ticketDeliveryMethodDTO2);
        ticketDeliveryMethodDTO2.setId(2L);
        assertThat(ticketDeliveryMethodDTO1).isNotEqualTo(ticketDeliveryMethodDTO2);
        ticketDeliveryMethodDTO1.setId(null);
        assertThat(ticketDeliveryMethodDTO1).isNotEqualTo(ticketDeliveryMethodDTO2);
    }
}
