package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class SupplyRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupplyRequest.class);
        SupplyRequest supplyRequest1 = new SupplyRequest();
        supplyRequest1.setId(1L);
        SupplyRequest supplyRequest2 = new SupplyRequest();
        supplyRequest2.setId(supplyRequest1.getId());
        assertThat(supplyRequest1).isEqualTo(supplyRequest2);
        supplyRequest2.setId(2L);
        assertThat(supplyRequest1).isNotEqualTo(supplyRequest2);
        supplyRequest1.setId(null);
        assertThat(supplyRequest1).isNotEqualTo(supplyRequest2);
    }
}
