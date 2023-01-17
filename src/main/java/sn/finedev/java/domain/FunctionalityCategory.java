package sn.finedev.java.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.finedev.java.domain.enumeration.FunctionalityCategoryStatus;

/**
 * A FunctionalityCategory.
 */
@Entity
@Table(name = "functionality_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "functionalitycategory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FunctionalityCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "logo", nullable = false)
    private byte[] logo;

    @NotNull
    @Column(name = "logo_content_type", nullable = false)
    private String logoContentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FunctionalityCategoryStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FunctionalityCategory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public FunctionalityCategory logo(byte[] logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public FunctionalityCategory logoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
        return this;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public FunctionalityCategoryStatus getStatus() {
        return this.status;
    }

    public FunctionalityCategory status(FunctionalityCategoryStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(FunctionalityCategoryStatus status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionalityCategory)) {
            return false;
        }
        return id != null && id.equals(((FunctionalityCategory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FunctionalityCategory{" +
            "id=" + getId() +
            ", logo='" + getLogo() + "'" +
            ", logoContentType='" + getLogoContentType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
