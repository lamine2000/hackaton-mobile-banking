package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link sn.finedev.java.domain.InsuranceAndMicroCreditsContribution} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InsuranceAndMicroCreditsContributionDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    private InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActor;

    private PaymentDTO payment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public InsuranceAndMicroCreditsActorDTO getInsuranceAndMicroCreditsActor() {
        return insuranceAndMicroCreditsActor;
    }

    public void setInsuranceAndMicroCreditsActor(InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActor) {
        this.insuranceAndMicroCreditsActor = insuranceAndMicroCreditsActor;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InsuranceAndMicroCreditsContributionDTO)) {
            return false;
        }

        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = (InsuranceAndMicroCreditsContributionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, insuranceAndMicroCreditsContributionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InsuranceAndMicroCreditsContributionDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", insuranceAndMicroCreditsActor=" + getInsuranceAndMicroCreditsActor() +
            ", payment=" + getPayment() +
            "}";
    }
}
