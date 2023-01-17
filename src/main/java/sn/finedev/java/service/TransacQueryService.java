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
import sn.finedev.java.domain.Transac;
import sn.finedev.java.repository.TransacRepository;
import sn.finedev.java.repository.search.TransacSearchRepository;
import sn.finedev.java.service.criteria.TransacCriteria;
import sn.finedev.java.service.dto.TransacDTO;
import sn.finedev.java.service.mapper.TransacMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Transac} entities in the database.
 * The main input is a {@link TransacCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransacDTO} or a {@link Page} of {@link TransacDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransacQueryService extends QueryService<Transac> {

    private final Logger log = LoggerFactory.getLogger(TransacQueryService.class);

    private final TransacRepository transacRepository;

    private final TransacMapper transacMapper;

    private final TransacSearchRepository transacSearchRepository;

    public TransacQueryService(
        TransacRepository transacRepository,
        TransacMapper transacMapper,
        TransacSearchRepository transacSearchRepository
    ) {
        this.transacRepository = transacRepository;
        this.transacMapper = transacMapper;
        this.transacSearchRepository = transacSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransacDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransacDTO> findByCriteria(TransacCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Transac> specification = createSpecification(criteria);
        return transacMapper.toDto(transacRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransacDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransacDTO> findByCriteria(TransacCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Transac> specification = createSpecification(criteria);
        return transacRepository.findAll(specification, page).map(transacMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransacCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Transac> specification = createSpecification(criteria);
        return transacRepository.count(specification);
    }

    /**
     * Function to convert {@link TransacCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Transac> createSpecification(TransacCriteria criteria) {
        Specification<Transac> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Transac_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Transac_.code));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Transac_.createdBy));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Transac_.createdAt));
            }
            if (criteria.getReceiver() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiver(), Transac_.receiver));
            }
            if (criteria.getSender() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSender(), Transac_.sender));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Transac_.amount));
            }
            if (criteria.getCurrency() != null) {
                specification = specification.and(buildSpecification(criteria.getCurrency(), Transac_.currency));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Transac_.type));
            }
        }
        return specification;
    }
}
