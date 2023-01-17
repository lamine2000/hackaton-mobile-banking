package sn.finedev.java.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import sn.finedev.java.domain.MobileBankingActor;

public interface MobileBankingActorRepositoryWithBagRelationships {
    Optional<MobileBankingActor> fetchBagRelationships(Optional<MobileBankingActor> mobileBankingActor);

    List<MobileBankingActor> fetchBagRelationships(List<MobileBankingActor> mobileBankingActors);

    Page<MobileBankingActor> fetchBagRelationships(Page<MobileBankingActor> mobileBankingActors);
}
