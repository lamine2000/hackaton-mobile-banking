package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link sn.finedev.java.domain.InsuranceAndMicroCreditsActor} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InsuranceAndMicroCreditsActorDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] logo;

    private String logoContentType;

    @NotNull
    private String name;

    private String acronym;

    @Lob
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return logoContentType;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InsuranceAndMicroCreditsActorDTO)) {
            return false;
        }

        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = (InsuranceAndMicroCreditsActorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, insuranceAndMicroCreditsActorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InsuranceAndMicroCreditsActorDTO{" +
            "id=" + getId() +
            ", logo='" + getLogo() + "'" +
            ", name='" + getName() + "'" +
            ", acronym='" + getAcronym() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
