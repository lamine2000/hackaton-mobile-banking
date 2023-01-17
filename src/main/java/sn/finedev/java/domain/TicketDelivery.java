package sn.finedev.java.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TicketDelivery.
 */
@Entity
@Table(name = "ticket_delivery")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ticketdelivery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "bought_at", nullable = false)
    private Instant boughtAt;

    @Column(name = "bought_by")
    private String boughtBy;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne
    private TicketDeliveryMethod ticketDeliveryMethod;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TicketDelivery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getBoughtAt() {
        return this.boughtAt;
    }

    public TicketDelivery boughtAt(Instant boughtAt) {
        this.setBoughtAt(boughtAt);
        return this;
    }

    public void setBoughtAt(Instant boughtAt) {
        this.boughtAt = boughtAt;
    }

    public String getBoughtBy() {
        return this.boughtBy;
    }

    public TicketDelivery boughtBy(String boughtBy) {
        this.setBoughtBy(boughtBy);
        return this;
    }

    public void setBoughtBy(String boughtBy) {
        this.boughtBy = boughtBy;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public TicketDelivery quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public TicketDeliveryMethod getTicketDeliveryMethod() {
        return this.ticketDeliveryMethod;
    }

    public void setTicketDeliveryMethod(TicketDeliveryMethod ticketDeliveryMethod) {
        this.ticketDeliveryMethod = ticketDeliveryMethod;
    }

    public TicketDelivery ticketDeliveryMethod(TicketDeliveryMethod ticketDeliveryMethod) {
        this.setTicketDeliveryMethod(ticketDeliveryMethod);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketDelivery)) {
            return false;
        }
        return id != null && id.equals(((TicketDelivery) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDelivery{" +
            "id=" + getId() +
            ", boughtAt='" + getBoughtAt() + "'" +
            ", boughtBy='" + getBoughtBy() + "'" +
            ", quantity=" + getQuantity() +
            "}";
    }
}
