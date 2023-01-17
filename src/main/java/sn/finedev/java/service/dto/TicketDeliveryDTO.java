package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link sn.finedev.java.domain.TicketDelivery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketDeliveryDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant boughtAt;

    private String boughtBy;

    @NotNull
    private Integer quantity;

    private TicketDeliveryMethodDTO ticketDeliveryMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getBoughtAt() {
        return boughtAt;
    }

    public void setBoughtAt(Instant boughtAt) {
        this.boughtAt = boughtAt;
    }

    public String getBoughtBy() {
        return boughtBy;
    }

    public void setBoughtBy(String boughtBy) {
        this.boughtBy = boughtBy;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public TicketDeliveryMethodDTO getTicketDeliveryMethod() {
        return ticketDeliveryMethod;
    }

    public void setTicketDeliveryMethod(TicketDeliveryMethodDTO ticketDeliveryMethod) {
        this.ticketDeliveryMethod = ticketDeliveryMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketDeliveryDTO)) {
            return false;
        }

        TicketDeliveryDTO ticketDeliveryDTO = (TicketDeliveryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketDeliveryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDeliveryDTO{" +
            "id=" + getId() +
            ", boughtAt='" + getBoughtAt() + "'" +
            ", boughtBy='" + getBoughtBy() + "'" +
            ", quantity=" + getQuantity() +
            ", ticketDeliveryMethod=" + getTicketDeliveryMethod() +
            "}";
    }
}
