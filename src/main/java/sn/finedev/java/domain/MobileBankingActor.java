package sn.finedev.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.finedev.java.domain.enumeration.MobileBankingActorStatus;

/**
 * A MobileBankingActor.
 */
@Entity
@Table(name = "mobile_banking_actor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mobilebankingactor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MobileBankingActor implements Serializable {

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
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MobileBankingActorStatus status;

    @ManyToMany
    @JoinTable(
        name = "rel_mobile_banking_actor__functionality",
        joinColumns = @JoinColumn(name = "mobile_banking_actor_id"),
        inverseJoinColumns = @JoinColumn(name = "functionality_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "functionalityCategory", "mobileBankingActors" }, allowSetters = true)
    private Set<Functionality> functionalities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MobileBankingActor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public MobileBankingActor logo(byte[] logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public MobileBankingActor logoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
        return this;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public String getName() {
        return this.name;
    }

    public MobileBankingActor name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MobileBankingActorStatus getStatus() {
        return this.status;
    }

    public MobileBankingActor status(MobileBankingActorStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MobileBankingActorStatus status) {
        this.status = status;
    }

    public Set<Functionality> getFunctionalities() {
        return this.functionalities;
    }

    public void setFunctionalities(Set<Functionality> functionalities) {
        this.functionalities = functionalities;
    }

    public MobileBankingActor functionalities(Set<Functionality> functionalities) {
        this.setFunctionalities(functionalities);
        return this;
    }

    public MobileBankingActor addFunctionality(Functionality functionality) {
        this.functionalities.add(functionality);
        functionality.getMobileBankingActors().add(this);
        return this;
    }

    public MobileBankingActor removeFunctionality(Functionality functionality) {
        this.functionalities.remove(functionality);
        functionality.getMobileBankingActors().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MobileBankingActor)) {
            return false;
        }
        return id != null && id.equals(((MobileBankingActor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MobileBankingActor{" +
            "id=" + getId() +
            ", logo='" + getLogo() + "'" +
            ", logoContentType='" + getLogoContentType() + "'" +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
