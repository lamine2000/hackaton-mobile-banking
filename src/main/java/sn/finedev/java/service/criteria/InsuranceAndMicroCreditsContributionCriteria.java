package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.InsuranceAndMicroCreditsContribution} entity. This class is used
 * in {@link sn.finedev.java.web.rest.InsuranceAndMicroCreditsContributionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /insurance-and-micro-credits-contributions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InsuranceAndMicroCreditsContributionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private LongFilter insuranceAndMicroCreditsActorId;

    private LongFilter paymentId;

    private Boolean distinct;

    public InsuranceAndMicroCreditsContributionCriteria() {}

    public InsuranceAndMicroCreditsContributionCriteria(InsuranceAndMicroCreditsContributionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.insuranceAndMicroCreditsActorId =
            other.insuranceAndMicroCreditsActorId == null ? null : other.insuranceAndMicroCreditsActorId.copy();
        this.paymentId = other.paymentId == null ? null : other.paymentId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public InsuranceAndMicroCreditsContributionCriteria copy() {
        return new InsuranceAndMicroCreditsContributionCriteria(this);
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

    public LongFilter getInsuranceAndMicroCreditsActorId() {
        return insuranceAndMicroCreditsActorId;
    }

    public LongFilter insuranceAndMicroCreditsActorId() {
        if (insuranceAndMicroCreditsActorId == null) {
            insuranceAndMicroCreditsActorId = new LongFilter();
        }
        return insuranceAndMicroCreditsActorId;
    }

    public void setInsuranceAndMicroCreditsActorId(LongFilter insuranceAndMicroCreditsActorId) {
        this.insuranceAndMicroCreditsActorId = insuranceAndMicroCreditsActorId;
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
        final InsuranceAndMicroCreditsContributionCriteria that = (InsuranceAndMicroCreditsContributionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(insuranceAndMicroCreditsActorId, that.insuranceAndMicroCreditsActorId) &&
            Objects.equals(paymentId, that.paymentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, insuranceAndMicroCreditsActorId, paymentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InsuranceAndMicroCreditsContributionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (insuranceAndMicroCreditsActorId != null ? "insuranceAndMicroCreditsActorId=" + insuranceAndMicroCreditsActorId + ", " : "") +
            (paymentId != null ? "paymentId=" + paymentId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
