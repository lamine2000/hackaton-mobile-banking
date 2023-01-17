package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class AdminNetworkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AdminNetworkDTO.class);
        AdminNetworkDTO adminNetworkDTO1 = new AdminNetworkDTO();
        adminNetworkDTO1.setId(1L);
        AdminNetworkDTO adminNetworkDTO2 = new AdminNetworkDTO();
        assertThat(adminNetworkDTO1).isNotEqualTo(adminNetworkDTO2);
        adminNetworkDTO2.setId(adminNetworkDTO1.getId());
        assertThat(adminNetworkDTO1).isEqualTo(adminNetworkDTO2);
        adminNetworkDTO2.setId(2L);
        assertThat(adminNetworkDTO1).isNotEqualTo(adminNetworkDTO2);
        adminNetworkDTO1.setId(null);
        assertThat(adminNetworkDTO1).isNotEqualTo(adminNetworkDTO2);
    }
}
