package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TownMapperTest {

    private TownMapper townMapper;

    @BeforeEach
    public void setUp() {
        townMapper = new TownMapperImpl();
    }
}
