package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Zone;
import sn.finedev.java.service.dto.ZoneDTO;

/**
 * Mapper for the entity {@link Zone} and its DTO {@link ZoneDTO}.
 */
@Mapper(componentModel = "spring")
public interface ZoneMapper extends EntityMapper<ZoneDTO, Zone> {}
