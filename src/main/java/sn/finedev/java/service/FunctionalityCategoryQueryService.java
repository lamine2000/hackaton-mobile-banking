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
import sn.finedev.java.domain.FunctionalityCategory;
import sn.finedev.java.repository.FunctionalityCategoryRepository;
import sn.finedev.java.repository.search.FunctionalityCategorySearchRepository;
import sn.finedev.java.service.criteria.FunctionalityCategoryCriteria;
import sn.finedev.java.service.dto.FunctionalityCategoryDTO;
import sn.finedev.java.service.mapper.FunctionalityCategoryMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FunctionalityCategory} entities in the database.
 * The main input is a {@link FunctionalityCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FunctionalityCategoryDTO} or a {@link Page} of {@link FunctionalityCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FunctionalityCategoryQueryService extends QueryService<FunctionalityCategory> {

    private final Logger log = LoggerFactory.getLogger(FunctionalityCategoryQueryService.class);

    private final FunctionalityCategoryRepository functionalityCategoryRepository;

    private final FunctionalityCategoryMapper functionalityCategoryMapper;

    private final FunctionalityCategorySearchRepository functionalityCategorySearchRepository;

    public FunctionalityCategoryQueryService(
        FunctionalityCategoryRepository functionalityCategoryRepository,
        FunctionalityCategoryMapper functionalityCategoryMapper,
        FunctionalityCategorySearchRepository functionalityCategorySearchRepository
    ) {
        this.functionalityCategoryRepository = functionalityCategoryRepository;
        this.functionalityCategoryMapper = functionalityCategoryMapper;
        this.functionalityCategorySearchRepository = functionalityCategorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link FunctionalityCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FunctionalityCategoryDTO> findByCriteria(FunctionalityCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FunctionalityCategory> specification = createSpecification(criteria);
        return functionalityCategoryMapper.toDto(functionalityCategoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FunctionalityCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FunctionalityCategoryDTO> findByCriteria(FunctionalityCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FunctionalityCategory> specification = createSpecification(criteria);
        return functionalityCategoryRepository.findAll(specification, page).map(functionalityCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FunctionalityCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FunctionalityCategory> specification = createSpecification(criteria);
        return functionalityCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link FunctionalityCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FunctionalityCategory> createSpecification(FunctionalityCategoryCriteria criteria) {
        Specification<FunctionalityCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FunctionalityCategory_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), FunctionalityCategory_.status));
            }
        }
        return specification;
    }
}
