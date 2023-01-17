package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TownTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Town.class);
        Town town1 = new Town();
        town1.setId(1L);
        Town town2 = new Town();
        town2.setId(town1.getId());
        assertThat(town1).isEqualTo(town2);
        town2.setId(2L);
        assertThat(town1).isNotEqualTo(town2);
        town1.setId(null);
        assertThat(town1).isNotEqualTo(town2);
    }
}
