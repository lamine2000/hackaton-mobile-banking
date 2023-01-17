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
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.repository.FunctionalityRepository;
import sn.finedev.java.repository.search.FunctionalitySearchRepository;
import sn.finedev.java.service.criteria.FunctionalityCriteria;
import sn.finedev.java.service.dto.FunctionalityDTO;
import sn.finedev.java.service.mapper.FunctionalityMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Functionality} entities in the database.
 * The main input is a {@link FunctionalityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FunctionalityDTO} or a {@link Page} of {@link FunctionalityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FunctionalityQueryService extends QueryService<Functionality> {

    private final Logger log = LoggerFactory.getLogger(FunctionalityQueryService.class);

    private final FunctionalityRepository functionalityRepository;

    private final FunctionalityMapper functionalityMapper;

    private final FunctionalitySearchRepository functionalitySearchRepository;

    public FunctionalityQueryService(
        FunctionalityRepository functionalityRepository,
        FunctionalityMapper functionalityMapper,
        FunctionalitySearchRepository functionalitySearchRepository
    ) {
        this.functionalityRepository = functionalityRepository;
        this.functionalityMapper = functionalityMapper;
        this.functionalitySearchRepository = functionalitySearchRepository;
    }

    /**
     * Return a {@link List} of {@link FunctionalityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FunctionalityDTO> findByCriteria(FunctionalityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Functionality> specification = createSpecification(criteria);
        return functionalityMapper.toDto(functionalityRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FunctionalityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FunctionalityDTO> findByCriteria(FunctionalityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Functionality> specification = createSpecification(criteria);
        return functionalityRepository.findAll(specification, page).map(functionalityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FunctionalityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Functionality> specification = createSpecification(criteria);
        return functionalityRepository.count(specification);
    }

    /**
     * Function to convert {@link FunctionalityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Functionality> createSpecification(FunctionalityCriteria criteria) {
        Specification<Functionality> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Functionality_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Functionality_.status));
            }
            if (criteria.getFunctionalityCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFunctionalityCategoryId(),
                            root -> root.join(Functionality_.functionalityCategory, JoinType.LEFT).get(FunctionalityCategory_.id)
                        )
                    );
            }
            if (criteria.getMobileBankingActorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getMobileBankingActorId(),
                            root -> root.join(Functionality_.mobileBankingActors, JoinType.LEFT).get(MobileBankingActor_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
