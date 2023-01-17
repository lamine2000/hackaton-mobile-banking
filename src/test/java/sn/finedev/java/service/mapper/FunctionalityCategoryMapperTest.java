package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FunctionalityCategoryMapperTest {

    private FunctionalityCategoryMapper functionalityCategoryMapper;

    @BeforeEach
    public void setUp() {
        functionalityCategoryMapper = new FunctionalityCategoryMapperImpl();
    }
}
