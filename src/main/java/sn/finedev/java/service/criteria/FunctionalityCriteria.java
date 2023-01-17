package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import sn.finedev.java.domain.enumeration.FunctionalityStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.Functionality} entity. This class is used
 * in {@link sn.finedev.java.web.rest.FunctionalityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /functionalities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FunctionalityCriteria implements Serializable, Criteria {

    /**
     * Class for filtering FunctionalityStatus
     */
    public static class FunctionalityStatusFilter extends Filter<FunctionalityStatus> {

        public FunctionalityStatusFilter() {}

        public FunctionalityStatusFilter(FunctionalityStatusFilter filter) {
            super(filter);
        }

        @Override
        public FunctionalityStatusFilter copy() {
            return new FunctionalityStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private FunctionalityStatusFilter status;

    private LongFilter functionalityCategoryId;

    private LongFilter mobileBankingActorId;

    private Boolean distinct;

    public FunctionalityCriteria() {}

    public FunctionalityCriteria(FunctionalityCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.functionalityCategoryId = other.functionalityCategoryId == null ? null : other.functionalityCategoryId.copy();
        this.mobileBankingActorId = other.mobileBankingActorId == null ? null : other.mobileBankingActorId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FunctionalityCriteria copy() {
        return new FunctionalityCriteria(this);
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

    public FunctionalityStatusFilter getStatus() {
        return status;
    }

    public FunctionalityStatusFilter status() {
        if (status == null) {
            status = new FunctionalityStatusFilter();
        }
        return status;
    }

    public void setStatus(FunctionalityStatusFilter status) {
        this.status = status;
    }

    public LongFilter getFunctionalityCategoryId() {
        return functionalityCategoryId;
    }

    public LongFilter functionalityCategoryId() {
        if (functionalityCategoryId == null) {
            functionalityCategoryId = new LongFilter();
        }
        return functionalityCategoryId;
    }

    public void setFunctionalityCategoryId(LongFilter functionalityCategoryId) {
        this.functionalityCategoryId = functionalityCategoryId;
    }

    public LongFilter getMobileBankingActorId() {
        return mobileBankingActorId;
    }

    public LongFilter mobileBankingActorId() {
        if (mobileBankingActorId == null) {
            mobileBankingActorId = new LongFilter();
        }
        return mobileBankingActorId;
    }

    public void setMobileBankingActorId(LongFilter mobileBankingActorId) {
        this.mobileBankingActorId = mobileBankingActorId;
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
        final FunctionalityCriteria that = (FunctionalityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(functionalityCategoryId, that.functionalityCategoryId) &&
            Objects.equals(mobileBankingActorId, that.mobileBankingActorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, functionalityCategoryId, mobileBankingActorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FunctionalityCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (functionalityCategoryId != null ? "functionalityCategoryId=" + functionalityCategoryId + ", " : "") +
            (mobileBankingActorId != null ? "mobileBankingActorId=" + mobileBankingActorId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
