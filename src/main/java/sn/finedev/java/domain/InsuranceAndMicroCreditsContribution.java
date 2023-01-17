package sn.finedev.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A InsuranceAndMicroCreditsContribution.
 */
@Entity
@Table(name = "insurance_and_micro_credits_contribution")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "insuranceandmicrocreditscontribution")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InsuranceAndMicroCreditsContribution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne
    private InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor;

    @ManyToOne
    @JsonIgnoreProperties(value = { "transac", "paymentMethod" }, allowSetters = true)
    private Payment payment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InsuranceAndMicroCreditsContribution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public InsuranceAndMicroCreditsContribution code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public InsuranceAndMicroCreditsActor getInsuranceAndMicroCreditsActor() {
        return this.insuranceAndMicroCreditsActor;
    }

    public void setInsuranceAndMicroCreditsActor(InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor) {
        this.insuranceAndMicroCreditsActor = insuranceAndMicroCreditsActor;
    }

    public InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsActor(InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor) {
        this.setInsuranceAndMicroCreditsActor(insuranceAndMicroCreditsActor);
        return this;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public InsuranceAndMicroCreditsContribution payment(Payment payment) {
        this.setPayment(payment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InsuranceAndMicroCreditsContribution)) {
            return false;
        }
        return id != null && id.equals(((InsuranceAndMicroCreditsContribution) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InsuranceAndMicroCreditsContribution{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            "}";
    }
}
