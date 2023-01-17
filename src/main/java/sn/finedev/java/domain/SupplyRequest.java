package sn.finedev.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.finedev.java.domain.enumeration.SupplyRequestStatus;

/**
 * A SupplyRequest.
 */
@Entity
@Table(name = "supply_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "supplyrequest")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupplyRequestStatus status;

    @ManyToOne
    @JsonIgnoreProperties(value = { "functionalityCategory", "mobileBankingActors" }, allowSetters = true)
    private Functionality functionality;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SupplyRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return this.amount;
    }

    public SupplyRequest amount(Double amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public SupplyRequest quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public SupplyRequestStatus getStatus() {
        return this.status;
    }

    public SupplyRequest status(SupplyRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SupplyRequestStatus status) {
        this.status = status;
    }

    public Functionality getFunctionality() {
        return this.functionality;
    }

    public void setFunctionality(Functionality functionality) {
        this.functionality = functionality;
    }

    public SupplyRequest functionality(Functionality functionality) {
        this.setFunctionality(functionality);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupplyRequest)) {
            return false;
        }
        return id != null && id.equals(((SupplyRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplyRequest{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", quantity=" + getQuantity() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
