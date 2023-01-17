package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.Notification} entity. This class is used
 * in {@link sn.finedev.java.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter message;

    private InstantFilter createdAt;

    private StringFilter sentTo;

    private StringFilter sentBy;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.message = other.message == null ? null : other.message.copy();
        this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
        this.sentTo = other.sentTo == null ? null : other.sentTo.copy();
        this.sentBy = other.sentBy == null ? null : other.sentBy.copy();
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getMessage() {
        return message;
    }

    public StringFilter message() {
        if (message == null) {
            message = new StringFilter();
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
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

    public StringFilter getSentTo() {
        return sentTo;
    }

    public StringFilter sentTo() {
        if (sentTo == null) {
            sentTo = new StringFilter();
        }
        return sentTo;
    }

    public void setSentTo(StringFilter sentTo) {
        this.sentTo = sentTo;
    }

    public StringFilter getSentBy() {
        return sentBy;
    }

    public StringFilter sentBy() {
        if (sentBy == null) {
            sentBy = new StringFilter();
        }
        return sentBy;
    }

    public void setSentBy(StringFilter sentBy) {
        this.sentBy = sentBy;
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
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(message, that.message) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(sentTo, that.sentTo) &&
            Objects.equals(sentBy, that.sentBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, message, createdAt, sentTo, sentBy, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (message != null ? "message=" + message + ", " : "") +
            (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
            (sentTo != null ? "sentTo=" + sentTo + ", " : "") +
            (sentBy != null ? "sentBy=" + sentBy + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
