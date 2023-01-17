package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsuranceAndMicroCreditsContributionMapperTest {

    private InsuranceAndMicroCreditsContributionMapper insuranceAndMicroCreditsContributionMapper;

    @BeforeEach
    public void setUp() {
        insuranceAndMicroCreditsContributionMapper = new InsuranceAndMicroCreditsContributionMapperImpl();
    }
}
