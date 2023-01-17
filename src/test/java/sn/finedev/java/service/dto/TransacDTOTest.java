package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class TransacDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransacDTO.class);
        TransacDTO transacDTO1 = new TransacDTO();
        transacDTO1.setId(1L);
        TransacDTO transacDTO2 = new TransacDTO();
        assertThat(transacDTO1).isNotEqualTo(transacDTO2);
        transacDTO2.setId(transacDTO1.getId());
        assertThat(transacDTO1).isEqualTo(transacDTO2);
        transacDTO2.setId(2L);
        assertThat(transacDTO1).isNotEqualTo(transacDTO2);
        transacDTO1.setId(null);
        assertThat(transacDTO1).isNotEqualTo(transacDTO2);
    }
}
