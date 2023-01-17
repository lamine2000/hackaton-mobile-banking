package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link sn.finedev.java.domain.Supply} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplyDTO implements Serializable {

    private Long id;

    @NotNull
    private String receiver;

    private SupplyRequestDTO supplyRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public SupplyRequestDTO getSupplyRequest() {
        return supplyRequest;
    }

    public void setSupplyRequest(SupplyRequestDTO supplyRequest) {
        this.supplyRequest = supplyRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupplyDTO)) {
            return false;
        }

        SupplyDTO supplyDTO = (SupplyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, supplyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplyDTO{" +
            "id=" + getId() +
            ", receiver='" + getReceiver() + "'" +
            ", supplyRequest=" + getSupplyRequest() +
            "}";
    }
}
