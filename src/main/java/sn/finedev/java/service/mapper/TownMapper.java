package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Town;
import sn.finedev.java.service.dto.TownDTO;

/**
 * Mapper for the entity {@link Town} and its DTO {@link TownDTO}.
 */
@Mapper(componentModel = "spring")
public interface TownMapper extends EntityMapper<TownDTO, Town> {}
