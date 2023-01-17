package sn.finedev.java.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.*; // for static metamodels
import sn.finedev.java.domain.FinalAgent;
import sn.finedev.java.repository.FinalAgentRepository;
import sn.finedev.java.repository.search.FinalAgentSearchRepository;
import sn.finedev.java.service.criteria.FinalAgentCriteria;
import sn.finedev.java.service.dto.FinalAgentDTO;
import sn.finedev.java.service.mapper.FinalAgentMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FinalAgent} entities in the database.
 * The main input is a {@link FinalAgentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FinalAgentDTO} or a {@link Page} of {@link FinalAgentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FinalAgentQueryService extends QueryService<FinalAgent> {

    private final Logger log = LoggerFactory.getLogger(FinalAgentQueryService.class);

    private final FinalAgentRepository finalAgentRepository;

    private final FinalAgentMapper finalAgentMapper;

    private final FinalAgentSearchRepository finalAgentSearchRepository;

    public FinalAgentQueryService(
        FinalAgentRepository finalAgentRepository,
        FinalAgentMapper finalAgentMapper,
        FinalAgentSearchRepository finalAgentSearchRepository
    ) {
        this.finalAgentRepository = finalAgentRepository;
        this.finalAgentMapper = finalAgentMapper;
        this.finalAgentSearchRepository = finalAgentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FinalAgentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FinalAgentDTO> findByCriteria(FinalAgentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FinalAgent> specification = createSpecification(criteria);
        return finalAgentMapper.toDto(finalAgentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FinalAgentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FinalAgentDTO> findByCriteria(FinalAgentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FinalAgent> specification = createSpecification(criteria);
        return finalAgentRepository.findAll(specification, page).map(finalAgentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FinalAgentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FinalAgent> specification = createSpecification(criteria);
        return finalAgentRepository.count(specification);
    }

    /**
     * Function to convert {@link FinalAgentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FinalAgent> createSpecification(FinalAgentCriteria criteria) {
        Specification<FinalAgent> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FinalAgent_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), FinalAgent_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), FinalAgent_.lastName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), FinalAgent_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), FinalAgent_.phone));
            }
            if (criteria.getAddressLine1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine1(), FinalAgent_.addressLine1));
            }
            if (criteria.getAddressLine2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine2(), FinalAgent_.addressLine2));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), FinalAgent_.city));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), FinalAgent_.status));
            }
            if (criteria.getCommissionRate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCommissionRate(), FinalAgent_.commissionRate));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(FinalAgent_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getStoreId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStoreId(), root -> root.join(FinalAgent_.store, JoinType.LEFT).get(Store_.id))
                    );
            }
        }
        return specification;
    }
}
