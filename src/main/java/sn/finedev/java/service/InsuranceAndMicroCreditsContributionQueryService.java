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
import sn.finedev.java.domain.InsuranceAndMicroCreditsContribution;
import sn.finedev.java.repository.InsuranceAndMicroCreditsContributionRepository;
import sn.finedev.java.repository.search.InsuranceAndMicroCreditsContributionSearchRepository;
import sn.finedev.java.service.criteria.InsuranceAndMicroCreditsContributionCriteria;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsContributionDTO;
import sn.finedev.java.service.mapper.InsuranceAndMicroCreditsContributionMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InsuranceAndMicroCreditsContribution} entities in the database.
 * The main input is a {@link InsuranceAndMicroCreditsContributionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InsuranceAndMicroCreditsContributionDTO} or a {@link Page} of {@link InsuranceAndMicroCreditsContributionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InsuranceAndMicroCreditsContributionQueryService extends QueryService<InsuranceAndMicroCreditsContribution> {

    private final Logger log = LoggerFactory.getLogger(InsuranceAndMicroCreditsContributionQueryService.class);

    private final InsuranceAndMicroCreditsContributionRepository insuranceAndMicroCreditsContributionRepository;

    private final InsuranceAndMicroCreditsContributionMapper insuranceAndMicroCreditsContributionMapper;

    private final InsuranceAndMicroCreditsContributionSearchRepository insuranceAndMicroCreditsContributionSearchRepository;

    public InsuranceAndMicroCreditsContributionQueryService(
        InsuranceAndMicroCreditsContributionRepository insuranceAndMicroCreditsContributionRepository,
        InsuranceAndMicroCreditsContributionMapper insuranceAndMicroCreditsContributionMapper,
        InsuranceAndMicroCreditsContributionSearchRepository insuranceAndMicroCreditsContributionSearchRepository
    ) {
        this.insuranceAndMicroCreditsContributionRepository = insuranceAndMicroCreditsContributionRepository;
        this.insuranceAndMicroCreditsContributionMapper = insuranceAndMicroCreditsContributionMapper;
        this.insuranceAndMicroCreditsContributionSearchRepository = insuranceAndMicroCreditsContributionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link InsuranceAndMicroCreditsContributionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InsuranceAndMicroCreditsContributionDTO> findByCriteria(InsuranceAndMicroCreditsContributionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<InsuranceAndMicroCreditsContribution> specification = createSpecification(criteria);
        return insuranceAndMicroCreditsContributionMapper.toDto(insuranceAndMicroCreditsContributionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InsuranceAndMicroCreditsContributionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InsuranceAndMicroCreditsContributionDTO> findByCriteria(
        InsuranceAndMicroCreditsContributionCriteria criteria,
        Pageable page
    ) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InsuranceAndMicroCreditsContribution> specification = createSpecification(criteria);
        return insuranceAndMicroCreditsContributionRepository
            .findAll(specification, page)
            .map(insuranceAndMicroCreditsContributionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InsuranceAndMicroCreditsContributionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<InsuranceAndMicroCreditsContribution> specification = createSpecification(criteria);
        return insuranceAndMicroCreditsContributionRepository.count(specification);
    }

    /**
     * Function to convert {@link InsuranceAndMicroCreditsContributionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InsuranceAndMicroCreditsContribution> createSpecification(
        InsuranceAndMicroCreditsContributionCriteria criteria
    ) {
        Specification<InsuranceAndMicroCreditsContribution> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), InsuranceAndMicroCreditsContribution_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), InsuranceAndMicroCreditsContribution_.code));
            }
            if (criteria.getInsuranceAndMicroCreditsActorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getInsuranceAndMicroCreditsActorId(),
                            root ->
                                root
                                    .join(InsuranceAndMicroCreditsContribution_.insuranceAndMicroCreditsActor, JoinType.LEFT)
                                    .get(InsuranceAndMicroCreditsActor_.id)
                        )
                    );
            }
            if (criteria.getPaymentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPaymentId(),
                            root -> root.join(InsuranceAndMicroCreditsContribution_.payment, JoinType.LEFT).get(Payment_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
