package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntermediateAgentMapperTest {

    private IntermediateAgentMapper intermediateAgentMapper;

    @BeforeEach
    public void setUp() {
        intermediateAgentMapper = new IntermediateAgentMapperImpl();
    }
}
