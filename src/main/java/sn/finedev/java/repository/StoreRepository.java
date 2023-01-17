package sn.finedev.java.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.Store;

/**
 * Spring Data JPA repository for the Store entity.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {
    default Optional<Store> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Store> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Store> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct store from Store store left join fetch store.zone left join fetch store.town left join fetch store.department left join fetch store.region left join fetch store.country",
        countQuery = "select count(distinct store) from Store store"
    )
    Page<Store> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct store from Store store left join fetch store.zone left join fetch store.town left join fetch store.department left join fetch store.region left join fetch store.country"
    )
    List<Store> findAllWithToOneRelationships();

    @Query(
        "select store from Store store left join fetch store.zone left join fetch store.town left join fetch store.department left join fetch store.region left join fetch store.country where store.id =:id"
    )
    Optional<Store> findOneWithToOneRelationships(@Param("id") Long id);
}
