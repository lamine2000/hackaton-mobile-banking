package sn.finedev.java.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.AdminNetwork;

/**
 * Spring Data JPA repository for the AdminNetwork entity.
 */
@Repository
public interface AdminNetworkRepository extends JpaRepository<AdminNetwork, Long>, JpaSpecificationExecutor<AdminNetwork> {
    default Optional<AdminNetwork> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AdminNetwork> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AdminNetwork> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct adminNetwork from AdminNetwork adminNetwork left join fetch adminNetwork.user",
        countQuery = "select count(distinct adminNetwork) from AdminNetwork adminNetwork"
    )
    Page<AdminNetwork> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct adminNetwork from AdminNetwork adminNetwork left join fetch adminNetwork.user")
    List<AdminNetwork> findAllWithToOneRelationships();

    @Query("select adminNetwork from AdminNetwork adminNetwork left join fetch adminNetwork.user where adminNetwork.id =:id")
    Optional<AdminNetwork> findOneWithToOneRelationships(@Param("id") Long id);
}
