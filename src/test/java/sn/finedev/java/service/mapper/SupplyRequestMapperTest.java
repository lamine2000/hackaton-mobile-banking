package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplyRequestMapperTest {

    private SupplyRequestMapper supplyRequestMapper;

    @BeforeEach
    public void setUp() {
        supplyRequestMapper = new SupplyRequestMapperImpl();
    }
}
