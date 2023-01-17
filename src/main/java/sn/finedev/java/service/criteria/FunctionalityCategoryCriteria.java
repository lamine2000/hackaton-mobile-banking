package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import sn.finedev.java.domain.enumeration.FunctionalityCategoryStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.FunctionalityCategory} entity. This class is used
 * in {@link sn.finedev.java.web.rest.FunctionalityCategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /functionality-categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FunctionalityCategoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering FunctionalityCategoryStatus
     */
    public static class FunctionalityCategoryStatusFilter extends Filter<FunctionalityCategoryStatus> {

        public FunctionalityCategoryStatusFilter() {}

        public FunctionalityCategoryStatusFilter(FunctionalityCategoryStatusFilter filter) {
            super(filter);
        }

        @Override
        public FunctionalityCategoryStatusFilter copy() {
            return new FunctionalityCategoryStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private FunctionalityCategoryStatusFilter status;

    private Boolean distinct;

    public FunctionalityCategoryCriteria() {}

    public FunctionalityCategoryCriteria(FunctionalityCategoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FunctionalityCategoryCriteria copy() {
        return new FunctionalityCategoryCriteria(this);
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

    public FunctionalityCategoryStatusFilter getStatus() {
        return status;
    }

    public FunctionalityCategoryStatusFilter status() {
        if (status == null) {
            status = new FunctionalityCategoryStatusFilter();
        }
        return status;
    }

    public void setStatus(FunctionalityCategoryStatusFilter status) {
        this.status = status;
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
        final FunctionalityCategoryCriteria that = (FunctionalityCategoryCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(status, that.status) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FunctionalityCategoryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
