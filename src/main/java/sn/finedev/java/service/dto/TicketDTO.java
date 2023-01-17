package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import sn.finedev.java.domain.enumeration.TicketStatus;

/**
 * A DTO for the {@link sn.finedev.java.domain.Ticket} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketDTO implements Serializable {

    private Long id;

    private String code;

    @Lob
    private byte[] data;

    private String dataContentType;

    @NotNull
    private Double pricePerUnit;

    private Double finalAgentCommission;

    @NotNull
    private TicketStatus status;

    private EventDTO event;

    private PaymentDTO payment;

    private TicketDeliveryDTO ticketDelivery;

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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDataContentType() {
        return dataContentType;
    }

    public void setDataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getFinalAgentCommission() {
        return finalAgentCommission;
    }

    public void setFinalAgentCommission(Double finalAgentCommission) {
        this.finalAgentCommission = finalAgentCommission;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }

    public TicketDeliveryDTO getTicketDelivery() {
        return ticketDelivery;
    }

    public void setTicketDelivery(TicketDeliveryDTO ticketDelivery) {
        this.ticketDelivery = ticketDelivery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketDTO)) {
            return false;
        }

        TicketDTO ticketDTO = (TicketDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", data='" + getData() + "'" +
            ", pricePerUnit=" + getPricePerUnit() +
            ", finalAgentCommission=" + getFinalAgentCommission() +
            ", status='" + getStatus() + "'" +
            ", event=" + getEvent() +
            ", payment=" + getPayment() +
            ", ticketDelivery=" + getTicketDelivery() +
            "}";
    }
}
