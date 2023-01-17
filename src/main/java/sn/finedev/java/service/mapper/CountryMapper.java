package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Country;
import sn.finedev.java.service.dto.CountryDTO;

/**
 * Mapper for the entity {@link Country} and its DTO {@link CountryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CountryMapper extends EntityMapper<CountryDTO, Country> {}
