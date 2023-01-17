package sn.finedev.java.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import sn.finedev.java.domain.enumeration.CurrencyCode;
import sn.finedev.java.domain.enumeration.StoreStatus;

/**
 * A Store.
 */
@Entity
@Table(name = "store")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "store")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @Lob
    @Column(name = "location", nullable = false)
    private byte[] location;

    @NotNull
    @Column(name = "location_content_type", nullable = false)
    private String locationContentType;

    @Column(name = "address")
    private String address;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private CurrencyCode currency;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull
    @Column(name = "notification_email", nullable = false)
    private String notificationEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StoreStatus status;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "about_us")
    private String aboutUs;

    @ManyToOne
    private Zone zone;

    @ManyToOne
    private Town town;

    @ManyToOne
    private Department department;

    @ManyToOne
    private Region region;

    @ManyToOne
    private Country country;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Store id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Store code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getLocation() {
        return this.location;
    }

    public Store location(byte[] location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(byte[] location) {
        this.location = location;
    }

    public String getLocationContentType() {
        return this.locationContentType;
    }

    public Store locationContentType(String locationContentType) {
        this.locationContentType = locationContentType;
        return this;
    }

    public void setLocationContentType(String locationContentType) {
        this.locationContentType = locationContentType;
    }

    public String getAddress() {
        return this.address;
    }

    public Store address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return this.name;
    }

    public Store name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Store description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CurrencyCode getCurrency() {
        return this.currency;
    }

    public Store currency(CurrencyCode currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public String getPhone() {
        return this.phone;
    }

    public Store phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotificationEmail() {
        return this.notificationEmail;
    }

    public Store notificationEmail(String notificationEmail) {
        this.setNotificationEmail(notificationEmail);
        return this;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public StoreStatus getStatus() {
        return this.status;
    }

    public Store status(StoreStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(StoreStatus status) {
        this.status = status;
    }

    public String getAboutUs() {
        return this.aboutUs;
    }

    public Store aboutUs(String aboutUs) {
        this.setAboutUs(aboutUs);
        return this;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public Zone getZone() {
        return this.zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Store zone(Zone zone) {
        this.setZone(zone);
        return this;
    }

    public Town getTown() {
        return this.town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Store town(Town town) {
        this.setTown(town);
        return this;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Store department(Department department) {
        this.setDepartment(department);
        return this;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Store region(Region region) {
        this.setRegion(region);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Store country(Country country) {
        this.setCountry(country);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Store)) {
            return false;
        }
        return id != null && id.equals(((Store) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Store{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", location='" + getLocation() + "'" +
            ", locationContentType='" + getLocationContentType() + "'" +
            ", address='" + getAddress() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", phone='" + getPhone() + "'" +
            ", notificationEmail='" + getNotificationEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", aboutUs='" + getAboutUs() + "'" +
            "}";
    }
}
