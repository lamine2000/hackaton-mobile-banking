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
import sn.finedev.java.domain.IntermediateAgent;
import sn.finedev.java.repository.IntermediateAgentRepository;
import sn.finedev.java.repository.search.IntermediateAgentSearchRepository;
import sn.finedev.java.service.criteria.IntermediateAgentCriteria;
import sn.finedev.java.service.dto.IntermediateAgentDTO;
import sn.finedev.java.service.mapper.IntermediateAgentMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link IntermediateAgent} entities in the database.
 * The main input is a {@link IntermediateAgentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link IntermediateAgentDTO} or a {@link Page} of {@link IntermediateAgentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IntermediateAgentQueryService extends QueryService<IntermediateAgent> {

    private final Logger log = LoggerFactory.getLogger(IntermediateAgentQueryService.class);

    private final IntermediateAgentRepository intermediateAgentRepository;

    private final IntermediateAgentMapper intermediateAgentMapper;

    private final IntermediateAgentSearchRepository intermediateAgentSearchRepository;

    public IntermediateAgentQueryService(
        IntermediateAgentRepository intermediateAgentRepository,
        IntermediateAgentMapper intermediateAgentMapper,
        IntermediateAgentSearchRepository intermediateAgentSearchRepository
    ) {
        this.intermediateAgentRepository = intermediateAgentRepository;
        this.intermediateAgentMapper = intermediateAgentMapper;
        this.intermediateAgentSearchRepository = intermediateAgentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link IntermediateAgentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<IntermediateAgentDTO> findByCriteria(IntermediateAgentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<IntermediateAgent> specification = createSpecification(criteria);
        return intermediateAgentMapper.toDto(intermediateAgentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link IntermediateAgentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IntermediateAgentDTO> findByCriteria(IntermediateAgentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<IntermediateAgent> specification = createSpecification(criteria);
        return intermediateAgentRepository.findAll(specification, page).map(intermediateAgentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IntermediateAgentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<IntermediateAgent> specification = createSpecification(criteria);
        return intermediateAgentRepository.count(specification);
    }

    /**
     * Function to convert {@link IntermediateAgentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<IntermediateAgent> createSpecification(IntermediateAgentCriteria criteria) {
        Specification<IntermediateAgent> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), IntermediateAgent_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), IntermediateAgent_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), IntermediateAgent_.lastName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), IntermediateAgent_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), IntermediateAgent_.phone));
            }
            if (criteria.getAddressLine1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine1(), IntermediateAgent_.addressLine1));
            }
            if (criteria.getAddressLine2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine2(), IntermediateAgent_.addressLine2));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), IntermediateAgent_.city));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), IntermediateAgent_.status));
            }
            if (criteria.getCommissionRate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCommissionRate(), IntermediateAgent_.commissionRate));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(IntermediateAgent_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getStoreId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStoreId(), root -> root.join(IntermediateAgent_.store, JoinType.LEFT).get(Store_.id))
                    );
            }
        }
        return specification;
    }
}
