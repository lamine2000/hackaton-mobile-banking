package sn.finedev.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.finedev.java.domain.enumeration.TicketStatus;

/**
 * A Ticket.
 */
@Entity
@Table(name = "ticket")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ticket")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    @NotNull
    @Column(name = "data_content_type", nullable = false)
    private String dataContentType;

    @NotNull
    @Column(name = "price_per_unit", nullable = false)
    private Double pricePerUnit;

    @Column(name = "final_agent_commission")
    private Double finalAgentCommission;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    @ManyToOne
    private Event event;

    @ManyToOne
    @JsonIgnoreProperties(value = { "transac", "paymentMethod" }, allowSetters = true)
    private Payment payment;

    @ManyToOne
    @JsonIgnoreProperties(value = { "ticketDeliveryMethod" }, allowSetters = true)
    private TicketDelivery ticketDelivery;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ticket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Ticket code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getData() {
        return this.data;
    }

    public Ticket data(byte[] data) {
        this.setData(data);
        return this;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDataContentType() {
        return this.dataContentType;
    }

    public Ticket dataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
        return this;
    }

    public void setDataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
    }

    public Double getPricePerUnit() {
        return this.pricePerUnit;
    }

    public Ticket pricePerUnit(Double pricePerUnit) {
        this.setPricePerUnit(pricePerUnit);
        return this;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getFinalAgentCommission() {
        return this.finalAgentCommission;
    }

    public Ticket finalAgentCommission(Double finalAgentCommission) {
        this.setFinalAgentCommission(finalAgentCommission);
        return this;
    }

    public void setFinalAgentCommission(Double finalAgentCommission) {
        this.finalAgentCommission = finalAgentCommission;
    }

    public TicketStatus getStatus() {
        return this.status;
    }

    public Ticket status(TicketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Ticket event(Event event) {
        this.setEvent(event);
        return this;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Ticket payment(Payment payment) {
        this.setPayment(payment);
        return this;
    }

    public TicketDelivery getTicketDelivery() {
        return this.ticketDelivery;
    }

    public void setTicketDelivery(TicketDelivery ticketDelivery) {
        this.ticketDelivery = ticketDelivery;
    }

    public Ticket ticketDelivery(TicketDelivery ticketDelivery) {
        this.setTicketDelivery(ticketDelivery);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return id != null && id.equals(((Ticket) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ticket{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", data='" + getData() + "'" +
            ", dataContentType='" + getDataContentType() + "'" +
            ", pricePerUnit=" + getPricePerUnit() +
            ", finalAgentCommission=" + getFinalAgentCommission() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
