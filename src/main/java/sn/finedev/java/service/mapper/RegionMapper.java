package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Region;
import sn.finedev.java.service.dto.RegionDTO;

/**
 * Mapper for the entity {@link Region} and its DTO {@link RegionDTO}.
 */
@Mapper(componentModel = "spring")
public interface RegionMapper extends EntityMapper<RegionDTO, Region> {}
