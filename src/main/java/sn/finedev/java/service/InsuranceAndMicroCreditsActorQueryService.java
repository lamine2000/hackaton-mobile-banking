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
import sn.finedev.java.domain.InsuranceAndMicroCreditsActor;
import sn.finedev.java.repository.InsuranceAndMicroCreditsActorRepository;
import sn.finedev.java.repository.search.InsuranceAndMicroCreditsActorSearchRepository;
import sn.finedev.java.service.criteria.InsuranceAndMicroCreditsActorCriteria;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsActorDTO;
import sn.finedev.java.service.mapper.InsuranceAndMicroCreditsActorMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InsuranceAndMicroCreditsActor} entities in the database.
 * The main input is a {@link InsuranceAndMicroCreditsActorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InsuranceAndMicroCreditsActorDTO} or a {@link Page} of {@link InsuranceAndMicroCreditsActorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InsuranceAndMicroCreditsActorQueryService extends QueryService<InsuranceAndMicroCreditsActor> {

    private final Logger log = LoggerFactory.getLogger(InsuranceAndMicroCreditsActorQueryService.class);

    private final InsuranceAndMicroCreditsActorRepository insuranceAndMicroCreditsActorRepository;

    private final InsuranceAndMicroCreditsActorMapper insuranceAndMicroCreditsActorMapper;

    private final InsuranceAndMicroCreditsActorSearchRepository insuranceAndMicroCreditsActorSearchRepository;

    public InsuranceAndMicroCreditsActorQueryService(
        InsuranceAndMicroCreditsActorRepository insuranceAndMicroCreditsActorRepository,
        InsuranceAndMicroCreditsActorMapper insuranceAndMicroCreditsActorMapper,
        InsuranceAndMicroCreditsActorSearchRepository insuranceAndMicroCreditsActorSearchRepository
    ) {
        this.insuranceAndMicroCreditsActorRepository = insuranceAndMicroCreditsActorRepository;
        this.insuranceAndMicroCreditsActorMapper = insuranceAndMicroCreditsActorMapper;
        this.insuranceAndMicroCreditsActorSearchRepository = insuranceAndMicroCreditsActorSearchRepository;
    }

    /**
     * Return a {@link List} of {@link InsuranceAndMicroCreditsActorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InsuranceAndMicroCreditsActorDTO> findByCriteria(InsuranceAndMicroCreditsActorCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<InsuranceAndMicroCreditsActor> specification = createSpecification(criteria);
        return insuranceAndMicroCreditsActorMapper.toDto(insuranceAndMicroCreditsActorRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InsuranceAndMicroCreditsActorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InsuranceAndMicroCreditsActorDTO> findByCriteria(InsuranceAndMicroCreditsActorCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InsuranceAndMicroCreditsActor> specification = createSpecification(criteria);
        return insuranceAndMicroCreditsActorRepository.findAll(specification, page).map(insuranceAndMicroCreditsActorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InsuranceAndMicroCreditsActorCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<InsuranceAndMicroCreditsActor> specification = createSpecification(criteria);
        return insuranceAndMicroCreditsActorRepository.count(specification);
    }

    /**
     * Function to convert {@link InsuranceAndMicroCreditsActorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InsuranceAndMicroCreditsActor> createSpecification(InsuranceAndMicroCreditsActorCriteria criteria) {
        Specification<InsuranceAndMicroCreditsActor> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), InsuranceAndMicroCreditsActor_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), InsuranceAndMicroCreditsActor_.name));
            }
            if (criteria.getAcronym() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAcronym(), InsuranceAndMicroCreditsActor_.acronym));
            }
        }
        return specification;
    }
}
