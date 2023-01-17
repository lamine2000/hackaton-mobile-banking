package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.AdminNetwork;
import sn.finedev.java.domain.User;
import sn.finedev.java.service.dto.AdminNetworkDTO;
import sn.finedev.java.service.dto.UserDTO;

/**
 * Mapper for the entity {@link AdminNetwork} and its DTO {@link AdminNetworkDTO}.
 */
@Mapper(componentModel = "spring")
public interface AdminNetworkMapper extends EntityMapper<AdminNetworkDTO, AdminNetwork> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    AdminNetworkDTO toDto(AdminNetwork s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
