package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class FinalAgentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinalAgent.class);
        FinalAgent finalAgent1 = new FinalAgent();
        finalAgent1.setId(1L);
        FinalAgent finalAgent2 = new FinalAgent();
        finalAgent2.setId(finalAgent1.getId());
        assertThat(finalAgent1).isEqualTo(finalAgent2);
        finalAgent2.setId(2L);
        assertThat(finalAgent1).isNotEqualTo(finalAgent2);
        finalAgent1.setId(null);
        assertThat(finalAgent1).isNotEqualTo(finalAgent2);
    }
}
