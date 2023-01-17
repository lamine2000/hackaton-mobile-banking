package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import sn.finedev.java.domain.enumeration.FunctionalityStatus;

/**
 * A DTO for the {@link sn.finedev.java.domain.Functionality} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FunctionalityDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] image;

    private String imageContentType;

    @NotNull
    private FunctionalityStatus status;

    private FunctionalityCategoryDTO functionalityCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public FunctionalityStatus getStatus() {
        return status;
    }

    public void setStatus(FunctionalityStatus status) {
        this.status = status;
    }

    public FunctionalityCategoryDTO getFunctionalityCategory() {
        return functionalityCategory;
    }

    public void setFunctionalityCategory(FunctionalityCategoryDTO functionalityCategory) {
        this.functionalityCategory = functionalityCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionalityDTO)) {
            return false;
        }

        FunctionalityDTO functionalityDTO = (FunctionalityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, functionalityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FunctionalityDTO{" +
            "id=" + getId() +
            ", image='" + getImage() + "'" +
            ", status='" + getStatus() + "'" +
            ", functionalityCategory=" + getFunctionalityCategory() +
            "}";
    }
}
