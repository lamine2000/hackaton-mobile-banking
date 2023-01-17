package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class InsuranceAndMicroCreditsContributionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InsuranceAndMicroCreditsContribution.class);
        InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsContribution1 = new InsuranceAndMicroCreditsContribution();
        insuranceAndMicroCreditsContribution1.setId(1L);
        InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsContribution2 = new InsuranceAndMicroCreditsContribution();
        insuranceAndMicroCreditsContribution2.setId(insuranceAndMicroCreditsContribution1.getId());
        assertThat(insuranceAndMicroCreditsContribution1).isEqualTo(insuranceAndMicroCreditsContribution2);
        insuranceAndMicroCreditsContribution2.setId(2L);
        assertThat(insuranceAndMicroCreditsContribution1).isNotEqualTo(insuranceAndMicroCreditsContribution2);
        insuranceAndMicroCreditsContribution1.setId(null);
        assertThat(insuranceAndMicroCreditsContribution1).isNotEqualTo(insuranceAndMicroCreditsContribution2);
    }
}
