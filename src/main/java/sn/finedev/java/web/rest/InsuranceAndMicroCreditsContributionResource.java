package sn.finedev.java.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.finedev.java.repository.InsuranceAndMicroCreditsContributionRepository;
import sn.finedev.java.service.InsuranceAndMicroCreditsContributionQueryService;
import sn.finedev.java.service.InsuranceAndMicroCreditsContributionService;
import sn.finedev.java.service.criteria.InsuranceAndMicroCreditsContributionCriteria;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsContributionDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.InsuranceAndMicroCreditsContribution}.
 */
@RestController
@RequestMapping("/api")
public class InsuranceAndMicroCreditsContributionResource {

    private final Logger log = LoggerFactory.getLogger(InsuranceAndMicroCreditsContributionResource.class);

    private static final String ENTITY_NAME = "insuranceAndMicroCreditsContribution";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InsuranceAndMicroCreditsContributionService insuranceAndMicroCreditsContributionService;

    private final InsuranceAndMicroCreditsContributionRepository insuranceAndMicroCreditsContributionRepository;

    private final InsuranceAndMicroCreditsContributionQueryService insuranceAndMicroCreditsContributionQueryService;

    public InsuranceAndMicroCreditsContributionResource(
        InsuranceAndMicroCreditsContributionService insuranceAndMicroCreditsContributionService,
        InsuranceAndMicroCreditsContributionRepository insuranceAndMicroCreditsContributionRepository,
        InsuranceAndMicroCreditsContributionQueryService insuranceAndMicroCreditsContributionQueryService
    ) {
        this.insuranceAndMicroCreditsContributionService = insuranceAndMicroCreditsContributionService;
        this.insuranceAndMicroCreditsContributionRepository = insuranceAndMicroCreditsContributionRepository;
        this.insuranceAndMicroCreditsContributionQueryService = insuranceAndMicroCreditsContributionQueryService;
    }

    /**
     * {@code POST  /insurance-and-micro-credits-contributions} : Create a new insuranceAndMicroCreditsContribution.
     *
     * @param insuranceAndMicroCreditsContributionDTO the insuranceAndMicroCreditsContributionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new insuranceAndMicroCreditsContributionDTO, or with status {@code 400 (Bad Request)} if the insuranceAndMicroCreditsContribution has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/insurance-and-micro-credits-contributions")
    public ResponseEntity<InsuranceAndMicroCreditsContributionDTO> createInsuranceAndMicroCreditsContribution(
        @Valid @RequestBody InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO
    ) throws URISyntaxException {
        log.debug("REST request to save InsuranceAndMicroCreditsContribution : {}", insuranceAndMicroCreditsContributionDTO);
        if (insuranceAndMicroCreditsContributionDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new insuranceAndMicroCreditsContribution cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        InsuranceAndMicroCreditsContributionDTO result = insuranceAndMicroCreditsContributionService.save(
            insuranceAndMicroCreditsContributionDTO
        );
        return ResponseEntity
            .created(new URI("/api/insurance-and-micro-credits-contributions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /insurance-and-micro-credits-contributions/:id} : Updates an existing insuranceAndMicroCreditsContribution.
     *
     * @param id the id of the insuranceAndMicroCreditsContributionDTO to save.
     * @param insuranceAndMicroCreditsContributionDTO the insuranceAndMicroCreditsContributionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated insuranceAndMicroCreditsContributionDTO,
     * or with status {@code 400 (Bad Request)} if the insuranceAndMicroCreditsContributionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the insuranceAndMicroCreditsContributionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/insurance-and-micro-credits-contributions/{id}")
    public ResponseEntity<InsuranceAndMicroCreditsContributionDTO> updateInsuranceAndMicroCreditsContribution(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update InsuranceAndMicroCreditsContribution : {}, {}", id, insuranceAndMicroCreditsContributionDTO);
        if (insuranceAndMicroCreditsContributionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, insuranceAndMicroCreditsContributionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!insuranceAndMicroCreditsContributionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InsuranceAndMicroCreditsContributionDTO result = insuranceAndMicroCreditsContributionService.update(
            insuranceAndMicroCreditsContributionDTO
        );
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    insuranceAndMicroCreditsContributionDTO.getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /insurance-and-micro-credits-contributions/:id} : Partial updates given fields of an existing insuranceAndMicroCreditsContribution, field will ignore if it is null
     *
     * @param id the id of the insuranceAndMicroCreditsContributionDTO to save.
     * @param insuranceAndMicroCreditsContributionDTO the insuranceAndMicroCreditsContributionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated insuranceAndMicroCreditsContributionDTO,
     * or with status {@code 400 (Bad Request)} if the insuranceAndMicroCreditsContributionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the insuranceAndMicroCreditsContributionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the insuranceAndMicroCreditsContributionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(
        value = "/insurance-and-micro-credits-contributions/{id}",
        consumes = { "application/json", "application/merge-patch+json" }
    )
    public ResponseEntity<InsuranceAndMicroCreditsContributionDTO> partialUpdateInsuranceAndMicroCreditsContribution(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update InsuranceAndMicroCreditsContribution partially : {}, {}",
            id,
            insuranceAndMicroCreditsContributionDTO
        );
        if (insuranceAndMicroCreditsContributionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, insuranceAndMicroCreditsContributionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!insuranceAndMicroCreditsContributionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InsuranceAndMicroCreditsContributionDTO> result = insuranceAndMicroCreditsContributionService.partialUpdate(
            insuranceAndMicroCreditsContributionDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(
                applicationName,
                true,
                ENTITY_NAME,
                insuranceAndMicroCreditsContributionDTO.getId().toString()
            )
        );
    }

    /**
     * {@code GET  /insurance-and-micro-credits-contributions} : get all the insuranceAndMicroCreditsContributions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of insuranceAndMicroCreditsContributions in body.
     */
    @GetMapping("/insurance-and-micro-credits-contributions")
    public ResponseEntity<List<InsuranceAndMicroCreditsContributionDTO>> getAllInsuranceAndMicroCreditsContributions(
        InsuranceAndMicroCreditsContributionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get InsuranceAndMicroCreditsContributions by criteria: {}", criteria);
        Page<InsuranceAndMicroCreditsContributionDTO> page = insuranceAndMicroCreditsContributionQueryService.findByCriteria(
            criteria,
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /insurance-and-micro-credits-contributions/count} : count all the insuranceAndMicroCreditsContributions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/insurance-and-micro-credits-contributions/count")
    public ResponseEntity<Long> countInsuranceAndMicroCreditsContributions(InsuranceAndMicroCreditsContributionCriteria criteria) {
        log.debug("REST request to count InsuranceAndMicroCreditsContributions by criteria: {}", criteria);
        return ResponseEntity.ok().body(insuranceAndMicroCreditsContributionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /insurance-and-micro-credits-contributions/:id} : get the "id" insuranceAndMicroCreditsContribution.
     *
     * @param id the id of the insuranceAndMicroCreditsContributionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the insuranceAndMicroCreditsContributionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/insurance-and-micro-credits-contributions/{id}")
    public ResponseEntity<InsuranceAndMicroCreditsContributionDTO> getInsuranceAndMicroCreditsContribution(@PathVariable Long id) {
        log.debug("REST request to get InsuranceAndMicroCreditsContribution : {}", id);
        Optional<InsuranceAndMicroCreditsContributionDTO> insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionService.findOne(
            id
        );
        return ResponseUtil.wrapOrNotFound(insuranceAndMicroCreditsContributionDTO);
    }

    /**
     * {@code DELETE  /insurance-and-micro-credits-contributions/:id} : delete the "id" insuranceAndMicroCreditsContribution.
     *
     * @param id the id of the insuranceAndMicroCreditsContributionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/insurance-and-micro-credits-contributions/{id}")
    public ResponseEntity<Void> deleteInsuranceAndMicroCreditsContribution(@PathVariable Long id) {
        log.debug("REST request to delete InsuranceAndMicroCreditsContribution : {}", id);
        insuranceAndMicroCreditsContributionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/insurance-and-micro-credits-contributions?query=:query} : search for the insuranceAndMicroCreditsContribution corresponding
     * to the query.
     *
     * @param query the query of the insuranceAndMicroCreditsContribution search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/insurance-and-micro-credits-contributions")
    public ResponseEntity<List<InsuranceAndMicroCreditsContributionDTO>> searchInsuranceAndMicroCreditsContributions(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of InsuranceAndMicroCreditsContributions for query {}", query);
        Page<InsuranceAndMicroCreditsContributionDTO> page = insuranceAndMicroCreditsContributionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
