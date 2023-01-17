package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsuranceAndMicroCreditsActorMapperTest {

    private InsuranceAndMicroCreditsActorMapper insuranceAndMicroCreditsActorMapper;

    @BeforeEach
    public void setUp() {
        insuranceAndMicroCreditsActorMapper = new InsuranceAndMicroCreditsActorMapperImpl();
    }
}
