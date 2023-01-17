package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TicketDeliveryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketDeliveryDTO.class);
        TicketDeliveryDTO ticketDeliveryDTO1 = new TicketDeliveryDTO();
        ticketDeliveryDTO1.setId(1L);
        TicketDeliveryDTO ticketDeliveryDTO2 = new TicketDeliveryDTO();
        assertThat(ticketDeliveryDTO1).isNotEqualTo(ticketDeliveryDTO2);
        ticketDeliveryDTO2.setId(ticketDeliveryDTO1.getId());
        assertThat(ticketDeliveryDTO1).isEqualTo(ticketDeliveryDTO2);
        ticketDeliveryDTO2.setId(2L);
        assertThat(ticketDeliveryDTO1).isNotEqualTo(ticketDeliveryDTO2);
        ticketDeliveryDTO1.setId(null);
        assertThat(ticketDeliveryDTO1).isNotEqualTo(ticketDeliveryDTO2);
    }
}
