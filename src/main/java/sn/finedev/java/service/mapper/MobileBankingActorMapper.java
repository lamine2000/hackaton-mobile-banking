package sn.finedev.java.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.domain.MobileBankingActor;
import sn.finedev.java.service.dto.FunctionalityDTO;
import sn.finedev.java.service.dto.MobileBankingActorDTO;

/**
 * Mapper for the entity {@link MobileBankingActor} and its DTO {@link MobileBankingActorDTO}.
 */
@Mapper(componentModel = "spring")
public interface MobileBankingActorMapper extends EntityMapper<MobileBankingActorDTO, MobileBankingActor> {
    @Mapping(target = "functionalities", source = "functionalities", qualifiedByName = "functionalityIdSet")
    MobileBankingActorDTO toDto(MobileBankingActor s);

    @Mapping(target = "removeFunctionality", ignore = true)
    MobileBankingActor toEntity(MobileBankingActorDTO mobileBankingActorDTO);

    @Named("functionalityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FunctionalityDTO toDtoFunctionalityId(Functionality functionality);

    @Named("functionalityIdSet")
    default Set<FunctionalityDTO> toDtoFunctionalityIdSet(Set<Functionality> functionality) {
        return functionality.stream().map(this::toDtoFunctionalityId).collect(Collectors.toSet());
    }
}
