package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Transac;
import sn.finedev.java.service.dto.TransacDTO;

/**
 * Mapper for the entity {@link Transac} and its DTO {@link TransacDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransacMapper extends EntityMapper<TransacDTO, Transac> {}
