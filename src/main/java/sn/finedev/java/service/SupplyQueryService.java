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
import sn.finedev.java.domain.Supply;
import sn.finedev.java.repository.SupplyRepository;
import sn.finedev.java.repository.search.SupplySearchRepository;
import sn.finedev.java.service.criteria.SupplyCriteria;
import sn.finedev.java.service.dto.SupplyDTO;
import sn.finedev.java.service.mapper.SupplyMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Supply} entities in the database.
 * The main input is a {@link SupplyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SupplyDTO} or a {@link Page} of {@link SupplyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SupplyQueryService extends QueryService<Supply> {

    private final Logger log = LoggerFactory.getLogger(SupplyQueryService.class);

    private final SupplyRepository supplyRepository;

    private final SupplyMapper supplyMapper;

    private final SupplySearchRepository supplySearchRepository;

    public SupplyQueryService(SupplyRepository supplyRepository, SupplyMapper supplyMapper, SupplySearchRepository supplySearchRepository) {
        this.supplyRepository = supplyRepository;
        this.supplyMapper = supplyMapper;
        this.supplySearchRepository = supplySearchRepository;
    }

    /**
     * Return a {@link List} of {@link SupplyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SupplyDTO> findByCriteria(SupplyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Supply> specification = createSpecification(criteria);
        return supplyMapper.toDto(supplyRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SupplyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SupplyDTO> findByCriteria(SupplyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Supply> specification = createSpecification(criteria);
        return supplyRepository.findAll(specification, page).map(supplyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SupplyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Supply> specification = createSpecification(criteria);
        return supplyRepository.count(specification);
    }

    /**
     * Function to convert {@link SupplyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Supply> createSpecification(SupplyCriteria criteria) {
        Specification<Supply> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Supply_.id));
            }
            if (criteria.getReceiver() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiver(), Supply_.receiver));
            }
            if (criteria.getSupplyRequestId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSupplyRequestId(),
                            root -> root.join(Supply_.supplyRequest, JoinType.LEFT).get(SupplyRequest_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
