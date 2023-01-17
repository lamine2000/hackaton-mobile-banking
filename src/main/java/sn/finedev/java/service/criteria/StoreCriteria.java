package sn.finedev.java.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import sn.finedev.java.domain.enumeration.CurrencyCode;
import sn.finedev.java.domain.enumeration.StoreStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.finedev.java.domain.Store} entity. This class is used
 * in {@link sn.finedev.java.web.rest.StoreResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stores?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoreCriteria implements Serializable, Criteria {

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
     * Class for filtering StoreStatus
     */
    public static class StoreStatusFilter extends Filter<StoreStatus> {

        public StoreStatusFilter() {}

        public StoreStatusFilter(StoreStatusFilter filter) {
            super(filter);
        }

        @Override
        public StoreStatusFilter copy() {
            return new StoreStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter address;

    private StringFilter name;

    private StringFilter description;

    private CurrencyCodeFilter currency;

    private StringFilter phone;

    private StringFilter notificationEmail;

    private StoreStatusFilter status;

    private LongFilter zoneId;

    private LongFilter townId;

    private LongFilter departmentId;

    private LongFilter regionId;

    private LongFilter countryId;

    private Boolean distinct;

    public StoreCriteria() {}

    public StoreCriteria(StoreCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.currency = other.currency == null ? null : other.currency.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.notificationEmail = other.notificationEmail == null ? null : other.notificationEmail.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.zoneId = other.zoneId == null ? null : other.zoneId.copy();
        this.townId = other.townId == null ? null : other.townId.copy();
        this.departmentId = other.departmentId == null ? null : other.departmentId.copy();
        this.regionId = other.regionId == null ? null : other.regionId.copy();
        this.countryId = other.countryId == null ? null : other.countryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StoreCriteria copy() {
        return new StoreCriteria(this);
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

    public StringFilter getAddress() {
        return address;
    }

    public StringFilter address() {
        if (address == null) {
            address = new StringFilter();
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
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

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
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

    public StringFilter getPhone() {
        return phone;
    }

    public StringFilter phone() {
        if (phone == null) {
            phone = new StringFilter();
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getNotificationEmail() {
        return notificationEmail;
    }

    public StringFilter notificationEmail() {
        if (notificationEmail == null) {
            notificationEmail = new StringFilter();
        }
        return notificationEmail;
    }

    public void setNotificationEmail(StringFilter notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public StoreStatusFilter getStatus() {
        return status;
    }

    public StoreStatusFilter status() {
        if (status == null) {
            status = new StoreStatusFilter();
        }
        return status;
    }

    public void setStatus(StoreStatusFilter status) {
        this.status = status;
    }

    public LongFilter getZoneId() {
        return zoneId;
    }

    public LongFilter zoneId() {
        if (zoneId == null) {
            zoneId = new LongFilter();
        }
        return zoneId;
    }

    public void setZoneId(LongFilter zoneId) {
        this.zoneId = zoneId;
    }

    public LongFilter getTownId() {
        return townId;
    }

    public LongFilter townId() {
        if (townId == null) {
            townId = new LongFilter();
        }
        return townId;
    }

    public void setTownId(LongFilter townId) {
        this.townId = townId;
    }

    public LongFilter getDepartmentId() {
        return departmentId;
    }

    public LongFilter departmentId() {
        if (departmentId == null) {
            departmentId = new LongFilter();
        }
        return departmentId;
    }

    public void setDepartmentId(LongFilter departmentId) {
        this.departmentId = departmentId;
    }

    public LongFilter getRegionId() {
        return regionId;
    }

    public LongFilter regionId() {
        if (regionId == null) {
            regionId = new LongFilter();
        }
        return regionId;
    }

    public void setRegionId(LongFilter regionId) {
        this.regionId = regionId;
    }

    public LongFilter getCountryId() {
        return countryId;
    }

    public LongFilter countryId() {
        if (countryId == null) {
            countryId = new LongFilter();
        }
        return countryId;
    }

    public void setCountryId(LongFilter countryId) {
        this.countryId = countryId;
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
        final StoreCriteria that = (StoreCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(address, that.address) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(notificationEmail, that.notificationEmail) &&
            Objects.equals(status, that.status) &&
            Objects.equals(zoneId, that.zoneId) &&
            Objects.equals(townId, that.townId) &&
            Objects.equals(departmentId, that.departmentId) &&
            Objects.equals(regionId, that.regionId) &&
            Objects.equals(countryId, that.countryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            address,
            name,
            description,
            currency,
            phone,
            notificationEmail,
            status,
            zoneId,
            townId,
            departmentId,
            regionId,
            countryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoreCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            (address != null ? "address=" + address + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (currency != null ? "currency=" + currency + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (notificationEmail != null ? "notificationEmail=" + notificationEmail + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (zoneId != null ? "zoneId=" + zoneId + ", " : "") +
            (townId != null ? "townId=" + townId + ", " : "") +
            (departmentId != null ? "departmentId=" + departmentId + ", " : "") +
            (regionId != null ? "regionId=" + regionId + ", " : "") +
            (countryId != null ? "countryId=" + countryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
