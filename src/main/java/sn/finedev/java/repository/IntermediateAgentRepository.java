package sn.finedev.java.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.IntermediateAgent;

/**
 * Spring Data JPA repository for the IntermediateAgent entity.
 */
@Repository
public interface IntermediateAgentRepository extends JpaRepository<IntermediateAgent, Long>, JpaSpecificationExecutor<IntermediateAgent> {
    default Optional<IntermediateAgent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<IntermediateAgent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<IntermediateAgent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct intermediateAgent from IntermediateAgent intermediateAgent left join fetch intermediateAgent.user",
        countQuery = "select count(distinct intermediateAgent) from IntermediateAgent intermediateAgent"
    )
    Page<IntermediateAgent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct intermediateAgent from IntermediateAgent intermediateAgent left join fetch intermediateAgent.user")
    List<IntermediateAgent> findAllWithToOneRelationships();

    @Query(
        "select intermediateAgent from IntermediateAgent intermediateAgent left join fetch intermediateAgent.user where intermediateAgent.id =:id"
    )
    Optional<IntermediateAgent> findOneWithToOneRelationships(@Param("id") Long id);
}
