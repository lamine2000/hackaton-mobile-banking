package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Supply;
import sn.finedev.java.domain.SupplyRequest;
import sn.finedev.java.service.dto.SupplyDTO;
import sn.finedev.java.service.dto.SupplyRequestDTO;

/**
 * Mapper for the entity {@link Supply} and its DTO {@link SupplyDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplyMapper extends EntityMapper<SupplyDTO, Supply> {
    @Mapping(target = "supplyRequest", source = "supplyRequest", qualifiedByName = "supplyRequestId")
    SupplyDTO toDto(Supply s);

    @Named("supplyRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SupplyRequestDTO toDtoSupplyRequestId(SupplyRequest supplyRequest);
}
