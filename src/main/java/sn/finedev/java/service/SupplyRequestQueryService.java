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
import sn.finedev.java.domain.SupplyRequest;
import sn.finedev.java.repository.SupplyRequestRepository;
import sn.finedev.java.repository.search.SupplyRequestSearchRepository;
import sn.finedev.java.service.criteria.SupplyRequestCriteria;
import sn.finedev.java.service.dto.SupplyRequestDTO;
import sn.finedev.java.service.mapper.SupplyRequestMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SupplyRequest} entities in the database.
 * The main input is a {@link SupplyRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SupplyRequestDTO} or a {@link Page} of {@link SupplyRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SupplyRequestQueryService extends QueryService<SupplyRequest> {

    private final Logger log = LoggerFactory.getLogger(SupplyRequestQueryService.class);

    private final SupplyRequestRepository supplyRequestRepository;

    private final SupplyRequestMapper supplyRequestMapper;

    private final SupplyRequestSearchRepository supplyRequestSearchRepository;

    public SupplyRequestQueryService(
        SupplyRequestRepository supplyRequestRepository,
        SupplyRequestMapper supplyRequestMapper,
        SupplyRequestSearchRepository supplyRequestSearchRepository
    ) {
        this.supplyRequestRepository = supplyRequestRepository;
        this.supplyRequestMapper = supplyRequestMapper;
        this.supplyRequestSearchRepository = supplyRequestSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SupplyRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SupplyRequestDTO> findByCriteria(SupplyRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SupplyRequest> specification = createSpecification(criteria);
        return supplyRequestMapper.toDto(supplyRequestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SupplyRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplyRequestDTO> findByCriteria(SupplyRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SupplyRequest> specification = createSpecification(criteria);
        return supplyRequestRepository.findAll(specification, page).map(supplyRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SupplyRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SupplyRequest> specification = createSpecification(criteria);
        return supplyRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link SupplyRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SupplyRequest> createSpecification(SupplyRequestCriteria criteria) {
        Specification<SupplyRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SupplyRequest_.id));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), SupplyRequest_.amount));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), SupplyRequest_.quantity));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), SupplyRequest_.status));
            }
            if (criteria.getFunctionalityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFunctionalityId(),
                            root -> root.join(SupplyRequest_.functionality, JoinType.LEFT).get(Functionality_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
