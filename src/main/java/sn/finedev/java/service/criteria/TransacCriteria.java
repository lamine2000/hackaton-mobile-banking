package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import sn.finedev.java.domain.enumeration.CurrencyCode;
import sn.finedev.java.domain.enumeration.TransacType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.Transac} entity. This class is used
 * in {@link sn.finedev.java.web.rest.TransacResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transacs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransacCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CurrencyCode
     */
    public static class CurrencyCodeFilter extends Filter<CurrencyCode> {

        public CurrencyCodeFilter() {}

        public CurrencyCodeFilter(CurrencyCodeFilter filter) {
            super(filter);
        }

        @Override
        public CurrencyCodeFilter copy() {
            return new CurrencyCodeFilter(this);
        }
    }

    /**
     * Class for filtering TransacType
     */
    public static class TransacTypeFilter extends Filter<TransacType> {

        public TransacTypeFilter() {}

        public TransacTypeFilter(TransacTypeFilter filter) {
            super(filter);
        }

        @Override
        public TransacTypeFilter copy() {
            return new TransacTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter createdBy;

    private InstantFilter createdAt;

    private StringFilter receiver;

    private StringFilter sender;

    private DoubleFilter amount;

    private CurrencyCodeFilter currency;

    private TransacTypeFilter type;

    private Boolean distinct;

    public TransacCriteria() {}

    public TransacCriteria(TransacCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.receiver = other.receiver == null ? null : other.receiver.copy();
        this.sender = other.sender == null ? null : other.sender.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.currency = other.currency == null ? null : other.currency.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransacCriteria copy() {
        return new TransacCriteria(this);
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

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            createdAt = new InstantFilter();
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
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

    public StringFilter getSender() {
        return sender;
    }

    public StringFilter sender() {
        if (sender == null) {
            sender = new StringFilter();
        }
        return sender;
    }

    public void setSender(StringFilter sender) {
        this.sender = sender;
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

    public CurrencyCodeFilter getCurrency() {
        return currency;
    }

    public CurrencyCodeFilter currency() {
        if (currency == null) {
            currency = new CurrencyCodeFilter();
        }
        return currency;
    }

    public void setCurrency(CurrencyCodeFilter currency) {
        this.currency = currency;
    }

    public TransacTypeFilter getType() {
        return type;
    }

    public TransacTypeFilter type() {
        if (type == null) {
            type = new TransacTypeFilter();
        }
        return type;
    }

    public void setType(TransacTypeFilter type) {
        this.type = type;
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
        final TransacCriteria that = (TransacCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(receiver, that.receiver) &&
            Objects.equals(sender, that.sender) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(type, that.type) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, createdBy, createdAt, receiver, sender, amount, currency, type, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransacCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (receiver != null ? "receiver=" + receiver + ", " : "") +
            (sender != null ? "sender=" + sender + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (currency != null ? "currency=" + currency + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
