package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransacMapperTest {

    private TransacMapper transacMapper;

    @BeforeEach
    public void setUp() {
        transacMapper = new TransacMapperImpl();
    }
}
