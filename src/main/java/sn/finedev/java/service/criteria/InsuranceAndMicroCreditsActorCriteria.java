package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.InsuranceAndMicroCreditsActor} entity. This class is used
 * in {@link sn.finedev.java.web.rest.InsuranceAndMicroCreditsActorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /insurance-and-micro-credits-actors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InsuranceAndMicroCreditsActorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter acronym;

    private Boolean distinct;

    public InsuranceAndMicroCreditsActorCriteria() {}

    public InsuranceAndMicroCreditsActorCriteria(InsuranceAndMicroCreditsActorCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.acronym = other.acronym == null ? null : other.acronym.copy();
        this.distinct = other.distinct;
    }

    @Override
    public InsuranceAndMicroCreditsActorCriteria copy() {
        return new InsuranceAndMicroCreditsActorCriteria(this);
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

    public StringFilter getAcronym() {
        return acronym;
    }

    public StringFilter acronym() {
        if (acronym == null) {
            acronym = new StringFilter();
        }
        return acronym;
    }

    public void setAcronym(StringFilter acronym) {
        this.acronym = acronym;
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
        final InsuranceAndMicroCreditsActorCriteria that = (InsuranceAndMicroCreditsActorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(acronym, that.acronym) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, acronym, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InsuranceAndMicroCreditsActorCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (acronym != null ? "acronym=" + acronym + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
