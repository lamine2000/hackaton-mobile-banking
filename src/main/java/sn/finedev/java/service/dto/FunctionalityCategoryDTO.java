package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import sn.finedev.java.domain.enumeration.FunctionalityCategoryStatus;

/**
 * A DTO for the {@link sn.finedev.java.domain.FunctionalityCategory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FunctionalityCategoryDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] logo;

    private String logoContentType;

    @NotNull
    private FunctionalityCategoryStatus status;

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

    public FunctionalityCategoryStatus getStatus() {
        return status;
    }

    public void setStatus(FunctionalityCategoryStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionalityCategoryDTO)) {
            return false;
        }

        FunctionalityCategoryDTO functionalityCategoryDTO = (FunctionalityCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, functionalityCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FunctionalityCategoryDTO{" +
            "id=" + getId() +
            ", logo='" + getLogo() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
