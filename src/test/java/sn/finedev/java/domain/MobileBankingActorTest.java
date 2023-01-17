package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class MobileBankingActorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MobileBankingActor.class);
        MobileBankingActor mobileBankingActor1 = new MobileBankingActor();
        mobileBankingActor1.setId(1L);
        MobileBankingActor mobileBankingActor2 = new MobileBankingActor();
        mobileBankingActor2.setId(mobileBankingActor1.getId());
        assertThat(mobileBankingActor1).isEqualTo(mobileBankingActor2);
        mobileBankingActor2.setId(2L);
        assertThat(mobileBankingActor1).isNotEqualTo(mobileBankingActor2);
        mobileBankingActor1.setId(null);
        assertThat(mobileBankingActor1).isNotEqualTo(mobileBankingActor2);
    }
}
