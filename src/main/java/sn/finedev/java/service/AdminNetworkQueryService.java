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
import sn.finedev.java.domain.AdminNetwork;
import sn.finedev.java.repository.AdminNetworkRepository;
import sn.finedev.java.repository.search.AdminNetworkSearchRepository;
import sn.finedev.java.service.criteria.AdminNetworkCriteria;
import sn.finedev.java.service.dto.AdminNetworkDTO;
import sn.finedev.java.service.mapper.AdminNetworkMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AdminNetwork} entities in the database.
 * The main input is a {@link AdminNetworkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AdminNetworkDTO} or a {@link Page} of {@link AdminNetworkDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AdminNetworkQueryService extends QueryService<AdminNetwork> {

    private final Logger log = LoggerFactory.getLogger(AdminNetworkQueryService.class);

    private final AdminNetworkRepository adminNetworkRepository;

    private final AdminNetworkMapper adminNetworkMapper;

    private final AdminNetworkSearchRepository adminNetworkSearchRepository;

    public AdminNetworkQueryService(
        AdminNetworkRepository adminNetworkRepository,
        AdminNetworkMapper adminNetworkMapper,
        AdminNetworkSearchRepository adminNetworkSearchRepository
    ) {
        this.adminNetworkRepository = adminNetworkRepository;
        this.adminNetworkMapper = adminNetworkMapper;
        this.adminNetworkSearchRepository = adminNetworkSearchRepository;
    }

    /**
     * Return a {@link List} of {@link AdminNetworkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AdminNetworkDTO> findByCriteria(AdminNetworkCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AdminNetwork> specification = createSpecification(criteria);
        return adminNetworkMapper.toDto(adminNetworkRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AdminNetworkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AdminNetworkDTO> findByCriteria(AdminNetworkCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AdminNetwork> specification = createSpecification(criteria);
        return adminNetworkRepository.findAll(specification, page).map(adminNetworkMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AdminNetworkCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AdminNetwork> specification = createSpecification(criteria);
        return adminNetworkRepository.count(specification);
    }

    /**
     * Function to convert {@link AdminNetworkCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AdminNetwork> createSpecification(AdminNetworkCriteria criteria) {
        Specification<AdminNetwork> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AdminNetwork_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), AdminNetwork_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), AdminNetwork_.lastName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), AdminNetwork_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), AdminNetwork_.phone));
            }
            if (criteria.getAddressLine1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine1(), AdminNetwork_.addressLine1));
            }
            if (criteria.getAddressLine2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressLine2(), AdminNetwork_.addressLine2));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), AdminNetwork_.city));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), AdminNetwork_.status));
            }
            if (criteria.getCommissionRate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCommissionRate(), AdminNetwork_.commissionRate));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(AdminNetwork_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
