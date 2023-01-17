package sn.finedev.java.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;
import sn.finedev.java.domain.enumeration.CurrencyCode;
import sn.finedev.java.domain.enumeration.TransacType;

/**
 * A DTO for the {@link sn.finedev.java.domain.Transac} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransacDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private String createdBy;

    @NotNull
    private Instant createdAt;

    private String receiver;

    private String sender;

    @NotNull
    private Double amount;

    @NotNull
    private CurrencyCode currency;

    @NotNull
    private TransacType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public TransacType getType() {
        return type;
    }

    public void setType(TransacType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransacDTO)) {
            return false;
        }

        TransacDTO transacDTO = (TransacDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transacDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransacDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", receiver='" + getReceiver() + "'" +
            ", sender='" + getSender() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
