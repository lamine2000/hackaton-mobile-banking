package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class SupplyRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupplyRequestDTO.class);
        SupplyRequestDTO supplyRequestDTO1 = new SupplyRequestDTO();
        supplyRequestDTO1.setId(1L);
        SupplyRequestDTO supplyRequestDTO2 = new SupplyRequestDTO();
        assertThat(supplyRequestDTO1).isNotEqualTo(supplyRequestDTO2);
        supplyRequestDTO2.setId(supplyRequestDTO1.getId());
        assertThat(supplyRequestDTO1).isEqualTo(supplyRequestDTO2);
        supplyRequestDTO2.setId(2L);
        assertThat(supplyRequestDTO1).isNotEqualTo(supplyRequestDTO2);
        supplyRequestDTO1.setId(null);
        assertThat(supplyRequestDTO1).isNotEqualTo(supplyRequestDTO2);
    }
}
