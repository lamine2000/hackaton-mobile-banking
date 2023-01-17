package sn.finedev.java.service.mapper;

import org.mapstruct.*;
import sn.finedev.java.domain.IntermediateAgent;
import sn.finedev.java.domain.Store;
import sn.finedev.java.domain.User;
import sn.finedev.java.service.dto.IntermediateAgentDTO;
import sn.finedev.java.service.dto.StoreDTO;
import sn.finedev.java.service.dto.UserDTO;

/**
 * Mapper for the entity {@link IntermediateAgent} and its DTO {@link IntermediateAgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntermediateAgentMapper extends EntityMapper<IntermediateAgentDTO, IntermediateAgent> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "store", source = "store", qualifiedByName = "storeId")
    IntermediateAgentDTO toDto(IntermediateAgent s);

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
