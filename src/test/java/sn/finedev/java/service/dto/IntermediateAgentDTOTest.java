package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class IntermediateAgentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntermediateAgentDTO.class);
        IntermediateAgentDTO intermediateAgentDTO1 = new IntermediateAgentDTO();
        intermediateAgentDTO1.setId(1L);
        IntermediateAgentDTO intermediateAgentDTO2 = new IntermediateAgentDTO();
        assertThat(intermediateAgentDTO1).isNotEqualTo(intermediateAgentDTO2);
        intermediateAgentDTO2.setId(intermediateAgentDTO1.getId());
        assertThat(intermediateAgentDTO1).isEqualTo(intermediateAgentDTO2);
        intermediateAgentDTO2.setId(2L);
        assertThat(intermediateAgentDTO1).isNotEqualTo(intermediateAgentDTO2);
        intermediateAgentDTO1.setId(null);
        assertThat(intermediateAgentDTO1).isNotEqualTo(intermediateAgentDTO2);
    }
}
