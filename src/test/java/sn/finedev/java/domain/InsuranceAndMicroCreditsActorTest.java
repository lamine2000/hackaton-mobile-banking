package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class InsuranceAndMicroCreditsActorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InsuranceAndMicroCreditsActor.class);
        InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor1 = new InsuranceAndMicroCreditsActor();
        insuranceAndMicroCreditsActor1.setId(1L);
        InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor2 = new InsuranceAndMicroCreditsActor();
        insuranceAndMicroCreditsActor2.setId(insuranceAndMicroCreditsActor1.getId());
        assertThat(insuranceAndMicroCreditsActor1).isEqualTo(insuranceAndMicroCreditsActor2);
        insuranceAndMicroCreditsActor2.setId(2L);
        assertThat(insuranceAndMicroCreditsActor1).isNotEqualTo(insuranceAndMicroCreditsActor2);
        insuranceAndMicroCreditsActor1.setId(null);
        assertThat(insuranceAndMicroCreditsActor1).isNotEqualTo(insuranceAndMicroCreditsActor2);
    }
}
