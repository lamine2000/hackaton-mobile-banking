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
import sn.finedev.java.domain.Store;
import sn.finedev.java.repository.StoreRepository;
import sn.finedev.java.repository.search.StoreSearchRepository;
import sn.finedev.java.service.criteria.StoreCriteria;
import sn.finedev.java.service.dto.StoreDTO;
import sn.finedev.java.service.mapper.StoreMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Store} entities in the database.
 * The main input is a {@link StoreCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StoreDTO} or a {@link Page} of {@link StoreDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StoreQueryService extends QueryService<Store> {

    private final Logger log = LoggerFactory.getLogger(StoreQueryService.class);

    private final StoreRepository storeRepository;

    private final StoreMapper storeMapper;

    private final StoreSearchRepository storeSearchRepository;

    public StoreQueryService(StoreRepository storeRepository, StoreMapper storeMapper, StoreSearchRepository storeSearchRepository) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
        this.storeSearchRepository = storeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link StoreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StoreDTO> findByCriteria(StoreCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Store> specification = createSpecification(criteria);
        return storeMapper.toDto(storeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StoreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StoreDTO> findByCriteria(StoreCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Store> specification = createSpecification(criteria);
        return storeRepository.findAll(specification, page).map(storeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StoreCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Store> specification = createSpecification(criteria);
        return storeRepository.count(specification);
    }

    /**
     * Function to convert {@link StoreCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Store> createSpecification(StoreCriteria criteria) {
        Specification<Store> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Store_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Store_.code));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Store_.address));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Store_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Store_.description));
            }
            if (criteria.getCurrency() != null) {
                specification = specification.and(buildSpecification(criteria.getCurrency(), Store_.currency));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Store_.phone));
            }
            if (criteria.getNotificationEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotificationEmail(), Store_.notificationEmail));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Store_.status));
            }
            if (criteria.getZoneId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getZoneId(), root -> root.join(Store_.zone, JoinType.LEFT).get(Zone_.id))
                    );
            }
            if (criteria.getTownId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTownId(), root -> root.join(Store_.town, JoinType.LEFT).get(Town_.id))
                    );
            }
            if (criteria.getDepartmentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDepartmentId(),
                            root -> root.join(Store_.department, JoinType.LEFT).get(Department_.id)
                        )
                    );
            }
            if (criteria.getRegionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRegionId(), root -> root.join(Store_.region, JoinType.LEFT).get(Region_.id))
                    );
            }
            if (criteria.getCountryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCountryId(), root -> root.join(Store_.country, JoinType.LEFT).get(Country_.id))
                    );
            }
        }
        return specification;
    }
}
