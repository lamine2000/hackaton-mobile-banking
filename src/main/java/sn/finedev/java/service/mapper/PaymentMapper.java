package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Payment;
import sn.finedev.java.domain.PaymentMethod;
import sn.finedev.java.domain.Transac;
import sn.finedev.java.service.dto.PaymentDTO;
import sn.finedev.java.service.dto.PaymentMethodDTO;
import sn.finedev.java.service.dto.TransacDTO;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "transac", source = "transac", qualifiedByName = "transacId")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodId")
    PaymentDTO toDto(Payment s);

    @Named("transacId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TransacDTO toDtoTransacId(Transac transac);

    @Named("paymentMethodId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentMethodDTO toDtoPaymentMethodId(PaymentMethod paymentMethod);
}
