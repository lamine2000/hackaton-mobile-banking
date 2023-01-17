package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class IntermediateAgentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntermediateAgent.class);
        IntermediateAgent intermediateAgent1 = new IntermediateAgent();
        intermediateAgent1.setId(1L);
        IntermediateAgent intermediateAgent2 = new IntermediateAgent();
        intermediateAgent2.setId(intermediateAgent1.getId());
        assertThat(intermediateAgent1).isEqualTo(intermediateAgent2);
        intermediateAgent2.setId(2L);
        assertThat(intermediateAgent1).isNotEqualTo(intermediateAgent2);
        intermediateAgent1.setId(null);
        assertThat(intermediateAgent1).isNotEqualTo(intermediateAgent2);
    }
}
