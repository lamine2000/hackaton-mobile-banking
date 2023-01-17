package sn.finedev.java.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.finedev.java.domain.MobileBankingActor;

/**
 * Spring Data JPA repository for the MobileBankingActor entity.
 *
 * When extending this class, extend MobileBankingActorRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface MobileBankingActorRepository
    extends
        MobileBankingActorRepositoryWithBagRelationships,
        JpaRepository<MobileBankingActor, Long>,
        JpaSpecificationExecutor<MobileBankingActor> {
    default Optional<MobileBankingActor> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<MobileBankingActor> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<MobileBankingActor> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
