package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class AdminNetworkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AdminNetwork.class);
        AdminNetwork adminNetwork1 = new AdminNetwork();
        adminNetwork1.setId(1L);
        AdminNetwork adminNetwork2 = new AdminNetwork();
        adminNetwork2.setId(adminNetwork1.getId());
        assertThat(adminNetwork1).isEqualTo(adminNetwork2);
        adminNetwork2.setId(2L);
        assertThat(adminNetwork1).isNotEqualTo(adminNetwork2);
        adminNetwork1.setId(null);
        assertThat(adminNetwork1).isNotEqualTo(adminNetwork2);
    }
}
