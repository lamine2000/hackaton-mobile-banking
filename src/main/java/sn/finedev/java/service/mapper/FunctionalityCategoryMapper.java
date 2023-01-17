package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.FunctionalityCategory;
import sn.finedev.java.service.dto.FunctionalityCategoryDTO;

/**
 * Mapper for the entity {@link FunctionalityCategory} and its DTO {@link FunctionalityCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface FunctionalityCategoryMapper extends EntityMapper<FunctionalityCategoryDTO, FunctionalityCategory> {}
