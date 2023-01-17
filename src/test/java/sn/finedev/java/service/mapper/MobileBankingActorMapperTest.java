package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MobileBankingActorMapperTest {

    private MobileBankingActorMapper mobileBankingActorMapper;

    @BeforeEach
    public void setUp() {
        mobileBankingActorMapper = new MobileBankingActorMapperImpl();
    }
}
