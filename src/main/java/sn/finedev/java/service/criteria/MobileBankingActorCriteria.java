package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import sn.finedev.java.domain.enumeration.MobileBankingActorStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.MobileBankingActor} entity. This class is used
 * in {@link sn.finedev.java.web.rest.MobileBankingActorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /mobile-banking-actors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MobileBankingActorCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MobileBankingActorStatus
     */
    public static class MobileBankingActorStatusFilter extends Filter<MobileBankingActorStatus> {

        public MobileBankingActorStatusFilter() {}

        public MobileBankingActorStatusFilter(MobileBankingActorStatusFilter filter) {
            super(filter);
        }

        @Override
        public MobileBankingActorStatusFilter copy() {
            return new MobileBankingActorStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private MobileBankingActorStatusFilter status;

    private LongFilter functionalityId;

    private Boolean distinct;

    public MobileBankingActorCriteria() {}

    public MobileBankingActorCriteria(MobileBankingActorCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.functionalityId = other.functionalityId == null ? null : other.functionalityId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MobileBankingActorCriteria copy() {
        return new MobileBankingActorCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public MobileBankingActorStatusFilter getStatus() {
        return status;
    }

    public MobileBankingActorStatusFilter status() {
        if (status == null) {
            status = new MobileBankingActorStatusFilter();
        }
        return status;
    }

    public void setStatus(MobileBankingActorStatusFilter status) {
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
        final MobileBankingActorCriteria that = (MobileBankingActorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(status, that.status) &&
            Objects.equals(functionalityId, that.functionalityId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, functionalityId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MobileBankingActorCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (functionalityId != null ? "functionalityId=" + functionalityId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
