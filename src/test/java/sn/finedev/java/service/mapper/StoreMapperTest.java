package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoreMapperTest {

    private StoreMapper storeMapper;

    @BeforeEach
    public void setUp() {
        storeMapper = new StoreMapperImpl();
    }
}
