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
import sn.finedev.java.repository.MobileBankingActorRepository;
import sn.finedev.java.service.MobileBankingActorQueryService;
import sn.finedev.java.service.MobileBankingActorService;
import sn.finedev.java.service.criteria.MobileBankingActorCriteria;
import sn.finedev.java.service.dto.MobileBankingActorDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.MobileBankingActor}.
 */
@RestController
@RequestMapping("/api")
public class MobileBankingActorResource {

    private final Logger log = LoggerFactory.getLogger(MobileBankingActorResource.class);

    private static final String ENTITY_NAME = "mobileBankingActor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MobileBankingActorService mobileBankingActorService;

    private final MobileBankingActorRepository mobileBankingActorRepository;

    private final MobileBankingActorQueryService mobileBankingActorQueryService;

    public MobileBankingActorResource(
        MobileBankingActorService mobileBankingActorService,
        MobileBankingActorRepository mobileBankingActorRepository,
        MobileBankingActorQueryService mobileBankingActorQueryService
    ) {
        this.mobileBankingActorService = mobileBankingActorService;
        this.mobileBankingActorRepository = mobileBankingActorRepository;
        this.mobileBankingActorQueryService = mobileBankingActorQueryService;
    }

    /**
     * {@code POST  /mobile-banking-actors} : Create a new mobileBankingActor.
     *
     * @param mobileBankingActorDTO the mobileBankingActorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mobileBankingActorDTO, or with status {@code 400 (Bad Request)} if the mobileBankingActor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mobile-banking-actors")
    public ResponseEntity<MobileBankingActorDTO> createMobileBankingActor(@Valid @RequestBody MobileBankingActorDTO mobileBankingActorDTO)
        throws URISyntaxException {
        log.debug("REST request to save MobileBankingActor : {}", mobileBankingActorDTO);
        if (mobileBankingActorDTO.getId() != null) {
            throw new BadRequestAlertException("A new mobileBankingActor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MobileBankingActorDTO result = mobileBankingActorService.save(mobileBankingActorDTO);
        return ResponseEntity
            .created(new URI("/api/mobile-banking-actors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /mobile-banking-actors/:id} : Updates an existing mobileBankingActor.
     *
     * @param id the id of the mobileBankingActorDTO to save.
     * @param mobileBankingActorDTO the mobileBankingActorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mobileBankingActorDTO,
     * or with status {@code 400 (Bad Request)} if the mobileBankingActorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mobileBankingActorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mobile-banking-actors/{id}")
    public ResponseEntity<MobileBankingActorDTO> updateMobileBankingActor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MobileBankingActorDTO mobileBankingActorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MobileBankingActor : {}, {}", id, mobileBankingActorDTO);
        if (mobileBankingActorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mobileBankingActorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mobileBankingActorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MobileBankingActorDTO result = mobileBankingActorService.update(mobileBankingActorDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mobileBankingActorDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /mobile-banking-actors/:id} : Partial updates given fields of an existing mobileBankingActor, field will ignore if it is null
     *
     * @param id the id of the mobileBankingActorDTO to save.
     * @param mobileBankingActorDTO the mobileBankingActorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mobileBankingActorDTO,
     * or with status {@code 400 (Bad Request)} if the mobileBankingActorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mobileBankingActorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mobileBankingActorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/mobile-banking-actors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MobileBankingActorDTO> partialUpdateMobileBankingActor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MobileBankingActorDTO mobileBankingActorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MobileBankingActor partially : {}, {}", id, mobileBankingActorDTO);
        if (mobileBankingActorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mobileBankingActorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mobileBankingActorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MobileBankingActorDTO> result = mobileBankingActorService.partialUpdate(mobileBankingActorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mobileBankingActorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mobile-banking-actors} : get all the mobileBankingActors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mobileBankingActors in body.
     */
    @GetMapping("/mobile-banking-actors")
    public ResponseEntity<List<MobileBankingActorDTO>> getAllMobileBankingActors(
        MobileBankingActorCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get MobileBankingActors by criteria: {}", criteria);
        Page<MobileBankingActorDTO> page = mobileBankingActorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /mobile-banking-actors/count} : count all the mobileBankingActors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/mobile-banking-actors/count")
    public ResponseEntity<Long> countMobileBankingActors(MobileBankingActorCriteria criteria) {
        log.debug("REST request to count MobileBankingActors by criteria: {}", criteria);
        return ResponseEntity.ok().body(mobileBankingActorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /mobile-banking-actors/:id} : get the "id" mobileBankingActor.
     *
     * @param id the id of the mobileBankingActorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mobileBankingActorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mobile-banking-actors/{id}")
    public ResponseEntity<MobileBankingActorDTO> getMobileBankingActor(@PathVariable Long id) {
        log.debug("REST request to get MobileBankingActor : {}", id);
        Optional<MobileBankingActorDTO> mobileBankingActorDTO = mobileBankingActorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mobileBankingActorDTO);
    }

    /**
     * {@code DELETE  /mobile-banking-actors/:id} : delete the "id" mobileBankingActor.
     *
     * @param id the id of the mobileBankingActorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mobile-banking-actors/{id}")
    public ResponseEntity<Void> deleteMobileBankingActor(@PathVariable Long id) {
        log.debug("REST request to delete MobileBankingActor : {}", id);
        mobileBankingActorService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/mobile-banking-actors?query=:query} : search for the mobileBankingActor corresponding
     * to the query.
     *
     * @param query the query of the mobileBankingActor search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/mobile-banking-actors")
    public ResponseEntity<List<MobileBankingActorDTO>> searchMobileBankingActors(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of MobileBankingActors for query {}", query);
        Page<MobileBankingActorDTO> page = mobileBankingActorService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
