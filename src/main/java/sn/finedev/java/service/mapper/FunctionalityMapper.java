package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.domain.FunctionalityCategory;
import sn.finedev.java.service.dto.FunctionalityCategoryDTO;
import sn.finedev.java.service.dto.FunctionalityDTO;

/**
 * Mapper for the entity {@link Functionality} and its DTO {@link FunctionalityDTO}.
 */
@Mapper(componentModel = "spring")
public interface FunctionalityMapper extends EntityMapper<FunctionalityDTO, Functionality> {
    @Mapping(target = "functionalityCategory", source = "functionalityCategory", qualifiedByName = "functionalityCategoryId")
    FunctionalityDTO toDto(Functionality s);

    @Named("functionalityCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FunctionalityCategoryDTO toDtoFunctionalityCategoryId(FunctionalityCategory functionalityCategory);
}
