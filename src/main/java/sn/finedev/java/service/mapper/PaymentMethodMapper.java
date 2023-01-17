package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.PaymentMethod;
import sn.finedev.java.service.dto.PaymentMethodDTO;

/**
 * Mapper for the entity {@link PaymentMethod} and its DTO {@link PaymentMethodDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMethodMapper extends EntityMapper<PaymentMethodDTO, PaymentMethod> {}
