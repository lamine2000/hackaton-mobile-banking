package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.Supply} entity. This class is used
 * in {@link sn.finedev.java.web.rest.SupplyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /supplies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter receiver;

    private LongFilter supplyRequestId;

    private Boolean distinct;

    public SupplyCriteria() {}

    public SupplyCriteria(SupplyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.receiver = other.receiver == null ? null : other.receiver.copy();
        this.supplyRequestId = other.supplyRequestId == null ? null : other.supplyRequestId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SupplyCriteria copy() {
        return new SupplyCriteria(this);
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

    public StringFilter getReceiver() {
        return receiver;
    }

    public StringFilter receiver() {
        if (receiver == null) {
            receiver = new StringFilter();
        }
        return receiver;
    }

    public void setReceiver(StringFilter receiver) {
        this.receiver = receiver;
    }

    public LongFilter getSupplyRequestId() {
        return supplyRequestId;
    }

    public LongFilter supplyRequestId() {
        if (supplyRequestId == null) {
            supplyRequestId = new LongFilter();
        }
        return supplyRequestId;
    }

    public void setSupplyRequestId(LongFilter supplyRequestId) {
        this.supplyRequestId = supplyRequestId;
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
        final SupplyCriteria that = (SupplyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(receiver, that.receiver) &&
            Objects.equals(supplyRequestId, that.supplyRequestId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, supplyRequestId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplyCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (receiver != null ? "receiver=" + receiver + ", " : "") +
            (supplyRequestId != null ? "supplyRequestId=" + supplyRequestId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
