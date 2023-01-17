package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class MobileBankingActorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MobileBankingActorDTO.class);
        MobileBankingActorDTO mobileBankingActorDTO1 = new MobileBankingActorDTO();
        mobileBankingActorDTO1.setId(1L);
        MobileBankingActorDTO mobileBankingActorDTO2 = new MobileBankingActorDTO();
        assertThat(mobileBankingActorDTO1).isNotEqualTo(mobileBankingActorDTO2);
        mobileBankingActorDTO2.setId(mobileBankingActorDTO1.getId());
        assertThat(mobileBankingActorDTO1).isEqualTo(mobileBankingActorDTO2);
        mobileBankingActorDTO2.setId(2L);
        assertThat(mobileBankingActorDTO1).isNotEqualTo(mobileBankingActorDTO2);
        mobileBankingActorDTO1.setId(null);
        assertThat(mobileBankingActorDTO1).isNotEqualTo(mobileBankingActorDTO2);
    }
}
