package sn.finedev.java.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.FinalAgent;

/**
 * Spring Data JPA repository for the FinalAgent entity.
 */
@Repository
public interface FinalAgentRepository extends JpaRepository<FinalAgent, Long>, JpaSpecificationExecutor<FinalAgent> {
    default Optional<FinalAgent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FinalAgent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FinalAgent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct finalAgent from FinalAgent finalAgent left join fetch finalAgent.user",
        countQuery = "select count(distinct finalAgent) from FinalAgent finalAgent"
    )
    Page<FinalAgent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct finalAgent from FinalAgent finalAgent left join fetch finalAgent.user")
    List<FinalAgent> findAllWithToOneRelationships();

    @Query("select finalAgent from FinalAgent finalAgent left join fetch finalAgent.user where finalAgent.id =:id")
    Optional<FinalAgent> findOneWithToOneRelationships(@Param("id") Long id);
}
