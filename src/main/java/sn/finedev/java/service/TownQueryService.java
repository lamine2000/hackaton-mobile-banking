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
import sn.finedev.java.domain.Town;
import sn.finedev.java.repository.TownRepository;
import sn.finedev.java.repository.search.TownSearchRepository;
import sn.finedev.java.service.criteria.TownCriteria;
import sn.finedev.java.service.dto.TownDTO;
import sn.finedev.java.service.mapper.TownMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Town} entities in the database.
 * The main input is a {@link TownCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TownDTO} or a {@link Page} of {@link TownDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TownQueryService extends QueryService<Town> {

    private final Logger log = LoggerFactory.getLogger(TownQueryService.class);

    private final TownRepository townRepository;

    private final TownMapper townMapper;

    private final TownSearchRepository townSearchRepository;

    public TownQueryService(TownRepository townRepository, TownMapper townMapper, TownSearchRepository townSearchRepository) {
        this.townRepository = townRepository;
        this.townMapper = townMapper;
        this.townSearchRepository = townSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TownDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TownDTO> findByCriteria(TownCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Town> specification = createSpecification(criteria);
        return townMapper.toDto(townRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TownDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TownDTO> findByCriteria(TownCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Town> specification = createSpecification(criteria);
        return townRepository.findAll(specification, page).map(townMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TownCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Town> specification = createSpecification(criteria);
        return townRepository.count(specification);
    }

    /**
     * Function to convert {@link TownCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Town> createSpecification(TownCriteria criteria) {
        Specification<Town> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Town_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Town_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Town_.code));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Town_.createdAt));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Town_.createdBy));
            }
        }
        return specification;
    }
}
