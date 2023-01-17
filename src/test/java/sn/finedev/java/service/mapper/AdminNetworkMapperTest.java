package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminNetworkMapperTest {

    private AdminNetworkMapper adminNetworkMapper;

    @BeforeEach
    public void setUp() {
        adminNetworkMapper = new AdminNetworkMapperImpl();
    }
}
