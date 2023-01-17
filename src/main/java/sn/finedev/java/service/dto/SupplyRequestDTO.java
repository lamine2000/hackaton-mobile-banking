package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import sn.finedev.java.domain.enumeration.SupplyRequestStatus;

/**
 * A DTO for the {@link sn.finedev.java.domain.SupplyRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplyRequestDTO implements Serializable {

    private Long id;

    private Double amount;

    private Integer quantity;

    @NotNull
    private SupplyRequestStatus status;

    private FunctionalityDTO functionality;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public SupplyRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SupplyRequestStatus status) {
        this.status = status;
    }

    public FunctionalityDTO getFunctionality() {
        return functionality;
    }

    public void setFunctionality(FunctionalityDTO functionality) {
        this.functionality = functionality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupplyRequestDTO)) {
            return false;
        }

        SupplyRequestDTO supplyRequestDTO = (SupplyRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, supplyRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplyRequestDTO{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", quantity=" + getQuantity() +
            ", status='" + getStatus() + "'" +
            ", functionality=" + getFunctionality() +
            "}";
    }
}
