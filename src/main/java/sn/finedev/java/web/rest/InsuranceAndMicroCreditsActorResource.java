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
import sn.finedev.java.repository.InsuranceAndMicroCreditsActorRepository;
import sn.finedev.java.service.InsuranceAndMicroCreditsActorQueryService;
import sn.finedev.java.service.InsuranceAndMicroCreditsActorService;
import sn.finedev.java.service.criteria.InsuranceAndMicroCreditsActorCriteria;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsActorDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.InsuranceAndMicroCreditsActor}.
 */
@RestController
@RequestMapping("/api")
public class InsuranceAndMicroCreditsActorResource {

    private final Logger log = LoggerFactory.getLogger(InsuranceAndMicroCreditsActorResource.class);

    private static final String ENTITY_NAME = "insuranceAndMicroCreditsActor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InsuranceAndMicroCreditsActorService insuranceAndMicroCreditsActorService;

    private final InsuranceAndMicroCreditsActorRepository insuranceAndMicroCreditsActorRepository;

    private final InsuranceAndMicroCreditsActorQueryService insuranceAndMicroCreditsActorQueryService;

    public InsuranceAndMicroCreditsActorResource(
        InsuranceAndMicroCreditsActorService insuranceAndMicroCreditsActorService,
        InsuranceAndMicroCreditsActorRepository insuranceAndMicroCreditsActorRepository,
        InsuranceAndMicroCreditsActorQueryService insuranceAndMicroCreditsActorQueryService
    ) {
        this.insuranceAndMicroCreditsActorService = insuranceAndMicroCreditsActorService;
        this.insuranceAndMicroCreditsActorRepository = insuranceAndMicroCreditsActorRepository;
        this.insuranceAndMicroCreditsActorQueryService = insuranceAndMicroCreditsActorQueryService;
    }

    /**
     * {@code POST  /insurance-and-micro-credits-actors} : Create a new insuranceAndMicroCreditsActor.
     *
     * @param insuranceAndMicroCreditsActorDTO the insuranceAndMicroCreditsActorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new insuranceAndMicroCreditsActorDTO, or with status {@code 400 (Bad Request)} if the insuranceAndMicroCreditsActor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/insurance-and-micro-credits-actors")
    public ResponseEntity<InsuranceAndMicroCreditsActorDTO> createInsuranceAndMicroCreditsActor(
        @Valid @RequestBody InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO
    ) throws URISyntaxException {
        log.debug("REST request to save InsuranceAndMicroCreditsActor : {}", insuranceAndMicroCreditsActorDTO);
        if (insuranceAndMicroCreditsActorDTO.getId() != null) {
            throw new BadRequestAlertException("A new insuranceAndMicroCreditsActor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InsuranceAndMicroCreditsActorDTO result = insuranceAndMicroCreditsActorService.save(insuranceAndMicroCreditsActorDTO);
        return ResponseEntity
            .created(new URI("/api/insurance-and-micro-credits-actors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /insurance-and-micro-credits-actors/:id} : Updates an existing insuranceAndMicroCreditsActor.
     *
     * @param id the id of the insuranceAndMicroCreditsActorDTO to save.
     * @param insuranceAndMicroCreditsActorDTO the insuranceAndMicroCreditsActorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated insuranceAndMicroCreditsActorDTO,
     * or with status {@code 400 (Bad Request)} if the insuranceAndMicroCreditsActorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the insuranceAndMicroCreditsActorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/insurance-and-micro-credits-actors/{id}")
    public ResponseEntity<InsuranceAndMicroCreditsActorDTO> updateInsuranceAndMicroCreditsActor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update InsuranceAndMicroCreditsActor : {}, {}", id, insuranceAndMicroCreditsActorDTO);
        if (insuranceAndMicroCreditsActorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, insuranceAndMicroCreditsActorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!insuranceAndMicroCreditsActorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InsuranceAndMicroCreditsActorDTO result = insuranceAndMicroCreditsActorService.update(insuranceAndMicroCreditsActorDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, insuranceAndMicroCreditsActorDTO.getId().toString())
            )
            .body(result);
    }

    /**
     * {@code PATCH  /insurance-and-micro-credits-actors/:id} : Partial updates given fields of an existing insuranceAndMicroCreditsActor, field will ignore if it is null
     *
     * @param id the id of the insuranceAndMicroCreditsActorDTO to save.
     * @param insuranceAndMicroCreditsActorDTO the insuranceAndMicroCreditsActorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated insuranceAndMicroCreditsActorDTO,
     * or with status {@code 400 (Bad Request)} if the insuranceAndMicroCreditsActorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the insuranceAndMicroCreditsActorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the insuranceAndMicroCreditsActorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/insurance-and-micro-credits-actors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InsuranceAndMicroCreditsActorDTO> partialUpdateInsuranceAndMicroCreditsActor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update InsuranceAndMicroCreditsActor partially : {}, {}", id, insuranceAndMicroCreditsActorDTO);
        if (insuranceAndMicroCreditsActorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, insuranceAndMicroCreditsActorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!insuranceAndMicroCreditsActorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InsuranceAndMicroCreditsActorDTO> result = insuranceAndMicroCreditsActorService.partialUpdate(
            insuranceAndMicroCreditsActorDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, insuranceAndMicroCreditsActorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /insurance-and-micro-credits-actors} : get all the insuranceAndMicroCreditsActors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of insuranceAndMicroCreditsActors in body.
     */
    @GetMapping("/insurance-and-micro-credits-actors")
    public ResponseEntity<List<InsuranceAndMicroCreditsActorDTO>> getAllInsuranceAndMicroCreditsActors(
        InsuranceAndMicroCreditsActorCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get InsuranceAndMicroCreditsActors by criteria: {}", criteria);
        Page<InsuranceAndMicroCreditsActorDTO> page = insuranceAndMicroCreditsActorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /insurance-and-micro-credits-actors/count} : count all the insuranceAndMicroCreditsActors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/insurance-and-micro-credits-actors/count")
    public ResponseEntity<Long> countInsuranceAndMicroCreditsActors(InsuranceAndMicroCreditsActorCriteria criteria) {
        log.debug("REST request to count InsuranceAndMicroCreditsActors by criteria: {}", criteria);
        return ResponseEntity.ok().body(insuranceAndMicroCreditsActorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /insurance-and-micro-credits-actors/:id} : get the "id" insuranceAndMicroCreditsActor.
     *
     * @param id the id of the insuranceAndMicroCreditsActorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the insuranceAndMicroCreditsActorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/insurance-and-micro-credits-actors/{id}")
    public ResponseEntity<InsuranceAndMicroCreditsActorDTO> getInsuranceAndMicroCreditsActor(@PathVariable Long id) {
        log.debug("REST request to get InsuranceAndMicroCreditsActor : {}", id);
        Optional<InsuranceAndMicroCreditsActorDTO> insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(insuranceAndMicroCreditsActorDTO);
    }

    /**
     * {@code DELETE  /insurance-and-micro-credits-actors/:id} : delete the "id" insuranceAndMicroCreditsActor.
     *
     * @param id the id of the insuranceAndMicroCreditsActorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/insurance-and-micro-credits-actors/{id}")
    public ResponseEntity<Void> deleteInsuranceAndMicroCreditsActor(@PathVariable Long id) {
        log.debug("REST request to delete InsuranceAndMicroCreditsActor : {}", id);
        insuranceAndMicroCreditsActorService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/insurance-and-micro-credits-actors?query=:query} : search for the insuranceAndMicroCreditsActor corresponding
     * to the query.
     *
     * @param query the query of the insuranceAndMicroCreditsActor search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/insurance-and-micro-credits-actors")
    public ResponseEntity<List<InsuranceAndMicroCreditsActorDTO>> searchInsuranceAndMicroCreditsActors(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of InsuranceAndMicroCreditsActors for query {}", query);
        Page<InsuranceAndMicroCreditsActorDTO> page = insuranceAndMicroCreditsActorService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
