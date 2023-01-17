package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.InsuranceAndMicroCreditsActor;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsActorDTO;

/**
 * Mapper for the entity {@link InsuranceAndMicroCreditsActor} and its DTO {@link InsuranceAndMicroCreditsActorDTO}.
 */
@Mapper(componentModel = "spring")
public interface InsuranceAndMicroCreditsActorMapper
    extends EntityMapper<InsuranceAndMicroCreditsActorDTO, InsuranceAndMicroCreditsActor> {}
