package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class FunctionalityCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FunctionalityCategoryDTO.class);
        FunctionalityCategoryDTO functionalityCategoryDTO1 = new FunctionalityCategoryDTO();
        functionalityCategoryDTO1.setId(1L);
        FunctionalityCategoryDTO functionalityCategoryDTO2 = new FunctionalityCategoryDTO();
        assertThat(functionalityCategoryDTO1).isNotEqualTo(functionalityCategoryDTO2);
        functionalityCategoryDTO2.setId(functionalityCategoryDTO1.getId());
        assertThat(functionalityCategoryDTO1).isEqualTo(functionalityCategoryDTO2);
        functionalityCategoryDTO2.setId(2L);
        assertThat(functionalityCategoryDTO1).isNotEqualTo(functionalityCategoryDTO2);
        functionalityCategoryDTO1.setId(null);
        assertThat(functionalityCategoryDTO1).isNotEqualTo(functionalityCategoryDTO2);
    }
}
