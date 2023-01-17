package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class InsuranceAndMicroCreditsActorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InsuranceAndMicroCreditsActorDTO.class);
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO1 = new InsuranceAndMicroCreditsActorDTO();
        insuranceAndMicroCreditsActorDTO1.setId(1L);
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO2 = new InsuranceAndMicroCreditsActorDTO();
        assertThat(insuranceAndMicroCreditsActorDTO1).isNotEqualTo(insuranceAndMicroCreditsActorDTO2);
        insuranceAndMicroCreditsActorDTO2.setId(insuranceAndMicroCreditsActorDTO1.getId());
        assertThat(insuranceAndMicroCreditsActorDTO1).isEqualTo(insuranceAndMicroCreditsActorDTO2);
        insuranceAndMicroCreditsActorDTO2.setId(2L);
        assertThat(insuranceAndMicroCreditsActorDTO1).isNotEqualTo(insuranceAndMicroCreditsActorDTO2);
        insuranceAndMicroCreditsActorDTO1.setId(null);
        assertThat(insuranceAndMicroCreditsActorDTO1).isNotEqualTo(insuranceAndMicroCreditsActorDTO2);
    }
}
