package sn.finedev.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Supply.
 */
@Entity
@Table(name = "supply")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "supply")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Supply implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "receiver", nullable = false)
    private String receiver;

    @ManyToOne
    @JsonIgnoreProperties(value = { "functionality" }, allowSetters = true)
    private SupplyRequest supplyRequest;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Supply id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public Supply receiver(String receiver) {
        this.setReceiver(receiver);
        return this;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public SupplyRequest getSupplyRequest() {
        return this.supplyRequest;
    }

    public void setSupplyRequest(SupplyRequest supplyRequest) {
        this.supplyRequest = supplyRequest;
    }

    public Supply supplyRequest(SupplyRequest supplyRequest) {
        this.setSupplyRequest(supplyRequest);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Supply)) {
            return false;
        }
        return id != null && id.equals(((Supply) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Supply{" +
            "id=" + getId() +
            ", receiver='" + getReceiver() + "'" +
            "}";
    }
}
