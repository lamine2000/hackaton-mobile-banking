package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.Department;
import sn.finedev.java.service.dto.DepartmentDTO;

/**
 * Mapper for the entity {@link Department} and its DTO {@link DepartmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper extends EntityMapper<DepartmentDTO, Department> {}
