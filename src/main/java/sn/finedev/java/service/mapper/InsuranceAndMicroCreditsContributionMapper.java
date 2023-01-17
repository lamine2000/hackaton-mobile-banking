package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.InsuranceAndMicroCreditsActor;
import sn.finedev.java.domain.InsuranceAndMicroCreditsContribution;
import sn.finedev.java.domain.Payment;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsActorDTO;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsContributionDTO;
import sn.finedev.java.service.dto.PaymentDTO;

/**
 * Mapper for the entity {@link InsuranceAndMicroCreditsContribution} and its DTO {@link InsuranceAndMicroCreditsContributionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InsuranceAndMicroCreditsContributionMapper
    extends EntityMapper<InsuranceAndMicroCreditsContributionDTO, InsuranceAndMicroCreditsContribution> {
    @Mapping(
        target = "insuranceAndMicroCreditsActor",
        source = "insuranceAndMicroCreditsActor",
        qualifiedByName = "insuranceAndMicroCreditsActorId"
    )
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    InsuranceAndMicroCreditsContributionDTO toDto(InsuranceAndMicroCreditsContribution s);

    @Named("insuranceAndMicroCreditsActorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InsuranceAndMicroCreditsActorDTO toDtoInsuranceAndMicroCreditsActorId(InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);
}
