package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import sn.finedev.java.domain.enumeration.MobileBankingActorStatus;

/**
 * A DTO for the {@link sn.finedev.java.domain.MobileBankingActor} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MobileBankingActorDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] logo;

    private String logoContentType;

    @NotNull
    private String name;

    @NotNull
    private MobileBankingActorStatus status;

    private Set<FunctionalityDTO> functionalities = new HashSet<>();

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

    public MobileBankingActorStatus getStatus() {
        return status;
    }

    public void setStatus(MobileBankingActorStatus status) {
        this.status = status;
    }

    public Set<FunctionalityDTO> getFunctionalities() {
        return functionalities;
    }

    public void setFunctionalities(Set<FunctionalityDTO> functionalities) {
        this.functionalities = functionalities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MobileBankingActorDTO)) {
            return false;
        }

        MobileBankingActorDTO mobileBankingActorDTO = (MobileBankingActorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mobileBankingActorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MobileBankingActorDTO{" +
            "id=" + getId() +
            ", logo='" + getLogo() + "'" +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            ", functionalities=" + getFunctionalities() +
            "}";
    }
}
