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
import sn.finedev.java.repository.FinalAgentRepository;
import sn.finedev.java.service.FinalAgentQueryService;
import sn.finedev.java.service.FinalAgentService;
import sn.finedev.java.service.criteria.FinalAgentCriteria;
import sn.finedev.java.service.dto.FinalAgentDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.FinalAgent}.
 */
@RestController
@RequestMapping("/api")
public class FinalAgentResource {

    private final Logger log = LoggerFactory.getLogger(FinalAgentResource.class);

    private static final String ENTITY_NAME = "finalAgent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FinalAgentService finalAgentService;

    private final FinalAgentRepository finalAgentRepository;

    private final FinalAgentQueryService finalAgentQueryService;

    public FinalAgentResource(
        FinalAgentService finalAgentService,
        FinalAgentRepository finalAgentRepository,
        FinalAgentQueryService finalAgentQueryService
    ) {
        this.finalAgentService = finalAgentService;
        this.finalAgentRepository = finalAgentRepository;
        this.finalAgentQueryService = finalAgentQueryService;
    }

    /**
     * {@code POST  /final-agents} : Create a new finalAgent.
     *
     * @param finalAgentDTO the finalAgentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new finalAgentDTO, or with status {@code 400 (Bad Request)} if the finalAgent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/final-agents")
    public ResponseEntity<FinalAgentDTO> createFinalAgent(@Valid @RequestBody FinalAgentDTO finalAgentDTO) throws URISyntaxException {
        log.debug("REST request to save FinalAgent : {}", finalAgentDTO);
        if (finalAgentDTO.getId() != null) {
            throw new BadRequestAlertException("A new finalAgent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FinalAgentDTO result = finalAgentService.save(finalAgentDTO);
        return ResponseEntity
            .created(new URI("/api/final-agents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /final-agents/:id} : Updates an existing finalAgent.
     *
     * @param id the id of the finalAgentDTO to save.
     * @param finalAgentDTO the finalAgentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated finalAgentDTO,
     * or with status {@code 400 (Bad Request)} if the finalAgentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the finalAgentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/final-agents/{id}")
    public ResponseEntity<FinalAgentDTO> updateFinalAgent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FinalAgentDTO finalAgentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FinalAgent : {}, {}", id, finalAgentDTO);
        if (finalAgentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, finalAgentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!finalAgentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FinalAgentDTO result = finalAgentService.update(finalAgentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, finalAgentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /final-agents/:id} : Partial updates given fields of an existing finalAgent, field will ignore if it is null
     *
     * @param id the id of the finalAgentDTO to save.
     * @param finalAgentDTO the finalAgentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated finalAgentDTO,
     * or with status {@code 400 (Bad Request)} if the finalAgentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the finalAgentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the finalAgentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/final-agents/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FinalAgentDTO> partialUpdateFinalAgent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FinalAgentDTO finalAgentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FinalAgent partially : {}, {}", id, finalAgentDTO);
        if (finalAgentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, finalAgentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!finalAgentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FinalAgentDTO> result = finalAgentService.partialUpdate(finalAgentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, finalAgentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /final-agents} : get all the finalAgents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of finalAgents in body.
     */
    @GetMapping("/final-agents")
    public ResponseEntity<List<FinalAgentDTO>> getAllFinalAgents(
        FinalAgentCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get FinalAgents by criteria: {}", criteria);
        Page<FinalAgentDTO> page = finalAgentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /final-agents/count} : count all the finalAgents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/final-agents/count")
    public ResponseEntity<Long> countFinalAgents(FinalAgentCriteria criteria) {
        log.debug("REST request to count FinalAgents by criteria: {}", criteria);
        return ResponseEntity.ok().body(finalAgentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /final-agents/:id} : get the "id" finalAgent.
     *
     * @param id the id of the finalAgentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the finalAgentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/final-agents/{id}")
    public ResponseEntity<FinalAgentDTO> getFinalAgent(@PathVariable Long id) {
        log.debug("REST request to get FinalAgent : {}", id);
        Optional<FinalAgentDTO> finalAgentDTO = finalAgentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(finalAgentDTO);
    }

    /**
     * {@code DELETE  /final-agents/:id} : delete the "id" finalAgent.
     *
     * @param id the id of the finalAgentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/final-agents/{id}")
    public ResponseEntity<Void> deleteFinalAgent(@PathVariable Long id) {
        log.debug("REST request to delete FinalAgent : {}", id);
        finalAgentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/final-agents?query=:query} : search for the finalAgent corresponding
     * to the query.
     *
     * @param query the query of the finalAgent search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/final-agents")
    public ResponseEntity<List<FinalAgentDTO>> searchFinalAgents(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of FinalAgents for query {}", query);
        Page<FinalAgentDTO> page = finalAgentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
