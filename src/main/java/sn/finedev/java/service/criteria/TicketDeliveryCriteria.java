package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.TicketDelivery} entity. This class is used
 * in {@link sn.finedev.java.web.rest.TicketDeliveryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ticket-deliveries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketDeliveryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter boughtAt;

    private StringFilter boughtBy;

    private IntegerFilter quantity;

    private LongFilter ticketDeliveryMethodId;

    private Boolean distinct;

    public TicketDeliveryCriteria() {}

    public TicketDeliveryCriteria(TicketDeliveryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.boughtAt = other.boughtAt == null ? null : other.boughtAt.copy();
        this.boughtBy = other.boughtBy == null ? null : other.boughtBy.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.ticketDeliveryMethodId = other.ticketDeliveryMethodId == null ? null : other.ticketDeliveryMethodId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TicketDeliveryCriteria copy() {
        return new TicketDeliveryCriteria(this);
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

    public InstantFilter getBoughtAt() {
        return boughtAt;
    }

    public InstantFilter boughtAt() {
        if (boughtAt == null) {
            boughtAt = new InstantFilter();
        }
        return boughtAt;
    }

    public void setBoughtAt(InstantFilter boughtAt) {
        this.boughtAt = boughtAt;
    }

    public StringFilter getBoughtBy() {
        return boughtBy;
    }

    public StringFilter boughtBy() {
        if (boughtBy == null) {
            boughtBy = new StringFilter();
        }
        return boughtBy;
    }

    public void setBoughtBy(StringFilter boughtBy) {
        this.boughtBy = boughtBy;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public LongFilter getTicketDeliveryMethodId() {
        return ticketDeliveryMethodId;
    }

    public LongFilter ticketDeliveryMethodId() {
        if (ticketDeliveryMethodId == null) {
            ticketDeliveryMethodId = new LongFilter();
        }
        return ticketDeliveryMethodId;
    }

    public void setTicketDeliveryMethodId(LongFilter ticketDeliveryMethodId) {
        this.ticketDeliveryMethodId = ticketDeliveryMethodId;
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
        final TicketDeliveryCriteria that = (TicketDeliveryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(boughtAt, that.boughtAt) &&
            Objects.equals(boughtBy, that.boughtBy) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(ticketDeliveryMethodId, that.ticketDeliveryMethodId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, boughtAt, boughtBy, quantity, ticketDeliveryMethodId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDeliveryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (boughtAt != null ? "boughtAt=" + boughtAt + ", " : "") +
            (boughtBy != null ? "boughtBy=" + boughtBy + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (ticketDeliveryMethodId != null ? "ticketDeliveryMethodId=" + ticketDeliveryMethodId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
