package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class SupplyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupplyDTO.class);
        SupplyDTO supplyDTO1 = new SupplyDTO();
        supplyDTO1.setId(1L);
        SupplyDTO supplyDTO2 = new SupplyDTO();
        assertThat(supplyDTO1).isNotEqualTo(supplyDTO2);
        supplyDTO2.setId(supplyDTO1.getId());
        assertThat(supplyDTO1).isEqualTo(supplyDTO2);
        supplyDTO2.setId(2L);
        assertThat(supplyDTO1).isNotEqualTo(supplyDTO2);
        supplyDTO1.setId(null);
        assertThat(supplyDTO1).isNotEqualTo(supplyDTO2);
    }
}
