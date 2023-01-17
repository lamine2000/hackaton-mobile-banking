package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class FinalAgentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinalAgentDTO.class);
        FinalAgentDTO finalAgentDTO1 = new FinalAgentDTO();
        finalAgentDTO1.setId(1L);
        FinalAgentDTO finalAgentDTO2 = new FinalAgentDTO();
        assertThat(finalAgentDTO1).isNotEqualTo(finalAgentDTO2);
        finalAgentDTO2.setId(finalAgentDTO1.getId());
        assertThat(finalAgentDTO1).isEqualTo(finalAgentDTO2);
        finalAgentDTO2.setId(2L);
        assertThat(finalAgentDTO1).isNotEqualTo(finalAgentDTO2);
        finalAgentDTO1.setId(null);
        assertThat(finalAgentDTO1).isNotEqualTo(finalAgentDTO2);
    }
}
