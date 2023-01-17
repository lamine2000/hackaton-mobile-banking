package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import sn.finedev.java.domain.enumeration.SupplyRequestStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.SupplyRequest} entity. This class is used
 * in {@link sn.finedev.java.web.rest.SupplyRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /supply-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplyRequestCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SupplyRequestStatus
     */
    public static class SupplyRequestStatusFilter extends Filter<SupplyRequestStatus> {

        public SupplyRequestStatusFilter() {}

        public SupplyRequestStatusFilter(SupplyRequestStatusFilter filter) {
            super(filter);
        }

        @Override
        public SupplyRequestStatusFilter copy() {
            return new SupplyRequestStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter amount;

    private IntegerFilter quantity;

    private SupplyRequestStatusFilter status;

    private LongFilter functionalityId;

    private Boolean distinct;

    public SupplyRequestCriteria() {}

    public SupplyRequestCriteria(SupplyRequestCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.functionalityId = other.functionalityId == null ? null : other.functionalityId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SupplyRequestCriteria copy() {
        return new SupplyRequestCriteria(this);
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

    public DoubleFilter getAmount() {
        return amount;
    }

    public DoubleFilter amount() {
        if (amount == null) {
            amount = new DoubleFilter();
        }
        return amount;
    }

    public void setAmount(DoubleFilter amount) {
        this.amount = amount;
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

    public SupplyRequestStatusFilter getStatus() {
        return status;
    }

    public SupplyRequestStatusFilter status() {
        if (status == null) {
            status = new SupplyRequestStatusFilter();
        }
        return status;
    }

    public void setStatus(SupplyRequestStatusFilter status) {
        this.status = status;
    }

    public LongFilter getFunctionalityId() {
        return functionalityId;
    }

    public LongFilter functionalityId() {
        if (functionalityId == null) {
            functionalityId = new LongFilter();
        }
        return functionalityId;
    }

    public void setFunctionalityId(LongFilter functionalityId) {
        this.functionalityId = functionalityId;
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
        final SupplyRequestCriteria that = (SupplyRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(status, that.status) &&
            Objects.equals(functionalityId, that.functionalityId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, quantity, status, functionalityId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplyRequestCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (functionalityId != null ? "functionalityId=" + functionalityId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
