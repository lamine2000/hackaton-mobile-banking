package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Country;
import sn.finedev.java.domain.Department;
import sn.finedev.java.domain.Region;
import sn.finedev.java.domain.Store;
import sn.finedev.java.domain.Town;
import sn.finedev.java.domain.Zone;
import sn.finedev.java.service.dto.CountryDTO;
import sn.finedev.java.service.dto.DepartmentDTO;
import sn.finedev.java.service.dto.RegionDTO;
import sn.finedev.java.service.dto.StoreDTO;
import sn.finedev.java.service.dto.TownDTO;
import sn.finedev.java.service.dto.ZoneDTO;

/**
 * Mapper for the entity {@link Store} and its DTO {@link StoreDTO}.
 */
@Mapper(componentModel = "spring")
public interface StoreMapper extends EntityMapper<StoreDTO, Store> {
    @Mapping(target = "zone", source = "zone", qualifiedByName = "zoneName")
    @Mapping(target = "town", source = "town", qualifiedByName = "townName")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "region", source = "region", qualifiedByName = "regionName")
    @Mapping(target = "country", source = "country", qualifiedByName = "countryName")
    StoreDTO toDto(Store s);

    @Named("zoneName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ZoneDTO toDtoZoneName(Zone zone);

    @Named("townName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TownDTO toDtoTownName(Town town);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);

    @Named("regionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RegionDTO toDtoRegionName(Region region);

    @Named("countryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CountryDTO toDtoCountryName(Country country);
}
