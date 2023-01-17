package sn.finedev.java.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import sn.finedev.java.domain.MobileBankingActor;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class MobileBankingActorRepositoryWithBagRelationshipsImpl implements MobileBankingActorRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<MobileBankingActor> fetchBagRelationships(Optional<MobileBankingActor> mobileBankingActor) {
        return mobileBankingActor.map(this::fetchFunctionalities);
    }

    @Override
    public Page<MobileBankingActor> fetchBagRelationships(Page<MobileBankingActor> mobileBankingActors) {
        return new PageImpl<>(
            fetchBagRelationships(mobileBankingActors.getContent()),
            mobileBankingActors.getPageable(),
            mobileBankingActors.getTotalElements()
        );
    }

    @Override
    public List<MobileBankingActor> fetchBagRelationships(List<MobileBankingActor> mobileBankingActors) {
        return Optional.of(mobileBankingActors).map(this::fetchFunctionalities).orElse(Collections.emptyList());
    }

    MobileBankingActor fetchFunctionalities(MobileBankingActor result) {
        return entityManager
            .createQuery(
                "select mobileBankingActor from MobileBankingActor mobileBankingActor left join fetch mobileBankingActor.functionalities where mobileBankingActor is :mobileBankingActor",
                MobileBankingActor.class
            )
            .setParameter("mobileBankingActor", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<MobileBankingActor> fetchFunctionalities(List<MobileBankingActor> mobileBankingActors) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, mobileBankingActors.size()).forEach(index -> order.put(mobileBankingActors.get(index).getId(), index));
        List<MobileBankingActor> result = entityManager
            .createQuery(
                "select distinct mobileBankingActor from MobileBankingActor mobileBankingActor left join fetch mobileBankingActor.functionalities where mobileBankingActor in :mobileBankingActors",
                MobileBankingActor.class
            )
            .setParameter("mobileBankingActors", mobileBankingActors)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
