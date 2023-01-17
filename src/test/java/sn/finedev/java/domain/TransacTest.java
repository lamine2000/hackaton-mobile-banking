package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TransacTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transac.class);
        Transac transac1 = new Transac();
        transac1.setId(1L);
        Transac transac2 = new Transac();
        transac2.setId(transac1.getId());
        assertThat(transac1).isEqualTo(transac2);
        transac2.setId(2L);
        assertThat(transac1).isNotEqualTo(transac2);
        transac1.setId(null);
        assertThat(transac1).isNotEqualTo(transac2);
    }
}
