package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class FunctionalityCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FunctionalityCategory.class);
        FunctionalityCategory functionalityCategory1 = new FunctionalityCategory();
        functionalityCategory1.setId(1L);
        FunctionalityCategory functionalityCategory2 = new FunctionalityCategory();
        functionalityCategory2.setId(functionalityCategory1.getId());
        assertThat(functionalityCategory1).isEqualTo(functionalityCategory2);
        functionalityCategory2.setId(2L);
        assertThat(functionalityCategory1).isNotEqualTo(functionalityCategory2);
        functionalityCategory1.setId(null);
        assertThat(functionalityCategory1).isNotEqualTo(functionalityCategory2);
    }
}
