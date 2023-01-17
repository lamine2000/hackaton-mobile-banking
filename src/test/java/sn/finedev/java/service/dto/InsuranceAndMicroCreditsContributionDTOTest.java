package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class InsuranceAndMicroCreditsContributionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InsuranceAndMicroCreditsContributionDTO.class);
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO1 = new InsuranceAndMicroCreditsContributionDTO();
        insuranceAndMicroCreditsContributionDTO1.setId(1L);
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO2 = new InsuranceAndMicroCreditsContributionDTO();
        assertThat(insuranceAndMicroCreditsContributionDTO1).isNotEqualTo(insuranceAndMicroCreditsContributionDTO2);
        insuranceAndMicroCreditsContributionDTO2.setId(insuranceAndMicroCreditsContributionDTO1.getId());
        assertThat(insuranceAndMicroCreditsContributionDTO1).isEqualTo(insuranceAndMicroCreditsContributionDTO2);
        insuranceAndMicroCreditsContributionDTO2.setId(2L);
        assertThat(insuranceAndMicroCreditsContributionDTO1).isNotEqualTo(insuranceAndMicroCreditsContributionDTO2);
        insuranceAndMicroCreditsContributionDTO1.setId(null);
        assertThat(insuranceAndMicroCreditsContributionDTO1).isNotEqualTo(insuranceAndMicroCreditsContributionDTO2);
    }
}
