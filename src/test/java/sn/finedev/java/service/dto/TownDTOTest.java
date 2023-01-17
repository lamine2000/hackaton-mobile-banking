package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TownDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TownDTO.class);
        TownDTO townDTO1 = new TownDTO();
        townDTO1.setId(1L);
        TownDTO townDTO2 = new TownDTO();
        assertThat(townDTO1).isNotEqualTo(townDTO2);
        townDTO2.setId(townDTO1.getId());
        assertThat(townDTO1).isEqualTo(townDTO2);
        townDTO2.setId(2L);
        assertThat(townDTO1).isNotEqualTo(townDTO2);
        townDTO1.setId(null);
        assertThat(townDTO1).isNotEqualTo(townDTO2);
    }
}
