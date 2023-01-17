package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.domain.SupplyRequest;
import sn.finedev.java.service.dto.FunctionalityDTO;
import sn.finedev.java.service.dto.SupplyRequestDTO;

/**
 * Mapper for the entity {@link SupplyRequest} and its DTO {@link SupplyRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplyRequestMapper extends EntityMapper<SupplyRequestDTO, SupplyRequest> {
    @Mapping(target = "functionality", source = "functionality", qualifiedByName = "functionalityId")
    SupplyRequestDTO toDto(SupplyRequest s);

    @Named("functionalityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FunctionalityDTO toDtoFunctionalityId(Functionality functionality);
}
