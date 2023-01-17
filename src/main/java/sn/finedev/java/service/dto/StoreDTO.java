package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import sn.finedev.java.domain.enumeration.CurrencyCode;
import sn.finedev.java.domain.enumeration.StoreStatus;

/**
 * A DTO for the {@link sn.finedev.java.domain.Store} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoreDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @Lob
    private byte[] location;

    private String locationContentType;
    private String address;

    @NotNull
    private String name;

    private String description;

    private CurrencyCode currency;

    @NotNull
    private String phone;

    @NotNull
    private String notificationEmail;

    private StoreStatus status;

    @Lob
    private String aboutUs;

    private ZoneDTO zone;

    private TownDTO town;

    private DepartmentDTO department;

    private RegionDTO region;

    private CountryDTO country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getLocation() {
        return location;
    }

    public void setLocation(byte[] location) {
        this.location = location;
    }

    public String getLocationContentType() {
        return locationContentType;
    }

    public void setLocationContentType(String locationContentType) {
        this.locationContentType = locationContentType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public StoreStatus getStatus() {
        return status;
    }

    public void setStatus(StoreStatus status) {
        this.status = status;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public ZoneDTO getZone() {
        return zone;
    }

    public void setZone(ZoneDTO zone) {
        this.zone = zone;
    }

    public TownDTO getTown() {
        return town;
    }

    public void setTown(TownDTO town) {
        this.town = town;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public RegionDTO getRegion() {
        return region;
    }

    public void setRegion(RegionDTO region) {
        this.region = region;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoreDTO)) {
            return false;
        }

        StoreDTO storeDTO = (StoreDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, storeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoreDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", location='" + getLocation() + "'" +
            ", address='" + getAddress() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", phone='" + getPhone() + "'" +
            ", notificationEmail='" + getNotificationEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", aboutUs='" + getAboutUs() + "'" +
            ", zone=" + getZone() +
            ", town=" + getTown() +
            ", department=" + getDepartment() +
            ", region=" + getRegion() +
            ", country=" + getCountry() +
            "}";
    }
}
