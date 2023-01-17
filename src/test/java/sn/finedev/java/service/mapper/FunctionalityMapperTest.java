package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FunctionalityMapperTest {

    private FunctionalityMapper functionalityMapper;

    @BeforeEach
    public void setUp() {
        functionalityMapper = new FunctionalityMapperImpl();
    }
}
