package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplyMapperTest {

    private SupplyMapper supplyMapper;

    @BeforeEach
    public void setUp() {
        supplyMapper = new SupplyMapperImpl();
    }
}
