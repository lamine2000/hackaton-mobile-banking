package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinalAgentMapperTest {

    private FinalAgentMapper finalAgentMapper;

    @BeforeEach
    public void setUp() {
        finalAgentMapper = new FinalAgentMapperImpl();
    }
}
