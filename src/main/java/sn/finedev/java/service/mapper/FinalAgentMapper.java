package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.FinalAgent;
import sn.finedev.java.domain.Store;
import sn.finedev.java.domain.User;
import sn.finedev.java.service.dto.FinalAgentDTO;
import sn.finedev.java.service.dto.StoreDTO;
import sn.finedev.java.service.dto.UserDTO;

/**
 * Mapper for the entity {@link FinalAgent} and its DTO {@link FinalAgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface FinalAgentMapper extends EntityMapper<FinalAgentDTO, FinalAgent> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "store", source = "store", qualifiedByName = "storeId")
    FinalAgentDTO toDto(FinalAgent s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("storeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StoreDTO toDtoStoreId(Store store);
}
