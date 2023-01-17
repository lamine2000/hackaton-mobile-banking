package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import sn.finedev.java.domain.enumeration.TicketStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.Ticket} entity. This class is used
 * in {@link sn.finedev.java.web.rest.TicketResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tickets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TicketStatus
     */
    public static class TicketStatusFilter extends Filter<TicketStatus> {

        public TicketStatusFilter() {}

        public TicketStatusFilter(TicketStatusFilter filter) {
            super(filter);
        }

        @Override
        public TicketStatusFilter copy() {
            return new TicketStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private DoubleFilter pricePerUnit;

    private DoubleFilter finalAgentCommission;

    private TicketStatusFilter status;

    private LongFilter eventId;

    private LongFilter paymentId;

    private LongFilter ticketDeliveryId;

    private Boolean distinct;

    public TicketCriteria() {}

    public TicketCriteria(TicketCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.pricePerUnit = other.pricePerUnit == null ? null : other.pricePerUnit.copy();
        this.finalAgentCommission = other.finalAgentCommission == null ? null : other.finalAgentCommission.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.paymentId = other.paymentId == null ? null : other.paymentId.copy();
        this.ticketDeliveryId = other.ticketDeliveryId == null ? null : other.ticketDeliveryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TicketCriteria copy() {
        return new TicketCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public StringFilter code() {
        if (code == null) {
            code = new StringFilter();
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public DoubleFilter getPricePerUnit() {
        return pricePerUnit;
    }

    public DoubleFilter pricePerUnit() {
        if (pricePerUnit == null) {
            pricePerUnit = new DoubleFilter();
        }
        return pricePerUnit;
    }

    public void setPricePerUnit(DoubleFilter pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public DoubleFilter getFinalAgentCommission() {
        return finalAgentCommission;
    }

    public DoubleFilter finalAgentCommission() {
        if (finalAgentCommission == null) {
            finalAgentCommission = new DoubleFilter();
        }
        return finalAgentCommission;
    }

    public void setFinalAgentCommission(DoubleFilter finalAgentCommission) {
        this.finalAgentCommission = finalAgentCommission;
    }

    public TicketStatusFilter getStatus() {
        return status;
    }

    public TicketStatusFilter status() {
        if (status == null) {
            status = new TicketStatusFilter();
        }
        return status;
    }

    public void setStatus(TicketStatusFilter status) {
        this.status = status;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public LongFilter eventId() {
        if (eventId == null) {
            eventId = new LongFilter();
        }
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
    }

    public LongFilter getPaymentId() {
        return paymentId;
    }

    public LongFilter paymentId() {
        if (paymentId == null) {
            paymentId = new LongFilter();
        }
        return paymentId;
    }

    public void setPaymentId(LongFilter paymentId) {
        this.paymentId = paymentId;
    }

    public LongFilter getTicketDeliveryId() {
        return ticketDeliveryId;
    }

    public LongFilter ticketDeliveryId() {
        if (ticketDeliveryId == null) {
            ticketDeliveryId = new LongFilter();
        }
        return ticketDeliveryId;
    }

    public void setTicketDeliveryId(LongFilter ticketDeliveryId) {
        this.ticketDeliveryId = ticketDeliveryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TicketCriteria that = (TicketCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(pricePerUnit, that.pricePerUnit) &&
            Objects.equals(finalAgentCommission, that.finalAgentCommission) &&
            Objects.equals(status, that.status) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(paymentId, that.paymentId) &&
            Objects.equals(ticketDeliveryId, that.ticketDeliveryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, pricePerUnit, finalAgentCommission, status, eventId, paymentId, ticketDeliveryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (pricePerUnit != null ? "pricePerUnit=" + pricePerUnit + ", " : "") +
            (finalAgentCommission != null ? "finalAgentCommission=" + finalAgentCommission + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (paymentId != null ? "paymentId=" + paymentId + ", " : "") +
            (ticketDeliveryId != null ? "ticketDeliveryId=" + ticketDeliveryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
