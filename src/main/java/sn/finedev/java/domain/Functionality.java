package sn.finedev.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.finedev.java.domain.enumeration.FunctionalityStatus;

/**
 * A Functionality.
 */
@Entity
@Table(name = "functionality")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "functionality")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Functionality implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

    @NotNull
    @Column(name = "image_content_type", nullable = false)
    private String imageContentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FunctionalityStatus status;

    @ManyToOne
    private FunctionalityCategory functionalityCategory;

    @ManyToMany(mappedBy = "functionalities")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "functionalities" }, allowSetters = true)
    private Set<MobileBankingActor> mobileBankingActors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Functionality id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Functionality image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Functionality imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public FunctionalityStatus getStatus() {
        return this.status;
    }

    public Functionality status(FunctionalityStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(FunctionalityStatus status) {
        this.status = status;
    }

    public FunctionalityCategory getFunctionalityCategory() {
        return this.functionalityCategory;
    }

    public void setFunctionalityCategory(FunctionalityCategory functionalityCategory) {
        this.functionalityCategory = functionalityCategory;
    }

    public Functionality functionalityCategory(FunctionalityCategory functionalityCategory) {
        this.setFunctionalityCategory(functionalityCategory);
        return this;
    }

    public Set<MobileBankingActor> getMobileBankingActors() {
        return this.mobileBankingActors;
    }

    public void setMobileBankingActors(Set<MobileBankingActor> mobileBankingActors) {
        if (this.mobileBankingActors != null) {
            this.mobileBankingActors.forEach(i -> i.removeFunctionality(this));
        }
        if (mobileBankingActors != null) {
            mobileBankingActors.forEach(i -> i.addFunctionality(this));
        }
        this.mobileBankingActors = mobileBankingActors;
    }

    public Functionality mobileBankingActors(Set<MobileBankingActor> mobileBankingActors) {
        this.setMobileBankingActors(mobileBankingActors);
        return this;
    }

    public Functionality addMobileBankingActor(MobileBankingActor mobileBankingActor) {
        this.mobileBankingActors.add(mobileBankingActor);
        mobileBankingActor.getFunctionalities().add(this);
        return this;
    }

    public Functionality removeMobileBankingActor(MobileBankingActor mobileBankingActor) {
        this.mobileBankingActors.remove(mobileBankingActor);
        mobileBankingActor.getFunctionalities().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Functionality)) {
            return false;
        }
        return id != null && id.equals(((Functionality) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Functionality{" +
            "id=" + getId() +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
