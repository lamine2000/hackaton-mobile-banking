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
import sn.finedev.java.repository.IntermediateAgentRepository;
import sn.finedev.java.service.IntermediateAgentQueryService;
import sn.finedev.java.service.IntermediateAgentService;
import sn.finedev.java.service.criteria.IntermediateAgentCriteria;
import sn.finedev.java.service.dto.IntermediateAgentDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.IntermediateAgent}.
 */
@RestController
@RequestMapping("/api")
public class IntermediateAgentResource {

    private final Logger log = LoggerFactory.getLogger(IntermediateAgentResource.class);

    private static final String ENTITY_NAME = "intermediateAgent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntermediateAgentService intermediateAgentService;

    private final IntermediateAgentRepository intermediateAgentRepository;

    private final IntermediateAgentQueryService intermediateAgentQueryService;

    public IntermediateAgentResource(
        IntermediateAgentService intermediateAgentService,
        IntermediateAgentRepository intermediateAgentRepository,
        IntermediateAgentQueryService intermediateAgentQueryService
    ) {
        this.intermediateAgentService = intermediateAgentService;
        this.intermediateAgentRepository = intermediateAgentRepository;
        this.intermediateAgentQueryService = intermediateAgentQueryService;
    }

    /**
     * {@code POST  /intermediate-agents} : Create a new intermediateAgent.
     *
     * @param intermediateAgentDTO the intermediateAgentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new intermediateAgentDTO, or with status {@code 400 (Bad Request)} if the intermediateAgent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/intermediate-agents")
    public ResponseEntity<IntermediateAgentDTO> createIntermediateAgent(@Valid @RequestBody IntermediateAgentDTO intermediateAgentDTO)
        throws URISyntaxException {
        log.debug("REST request to save IntermediateAgent : {}", intermediateAgentDTO);
        if (intermediateAgentDTO.getId() != null) {
            throw new BadRequestAlertException("A new intermediateAgent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IntermediateAgentDTO result = intermediateAgentService.save(intermediateAgentDTO);
        return ResponseEntity
            .created(new URI("/api/intermediate-agents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /intermediate-agents/:id} : Updates an existing intermediateAgent.
     *
     * @param id the id of the intermediateAgentDTO to save.
     * @param intermediateAgentDTO the intermediateAgentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intermediateAgentDTO,
     * or with status {@code 400 (Bad Request)} if the intermediateAgentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the intermediateAgentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/intermediate-agents/{id}")
    public ResponseEntity<IntermediateAgentDTO> updateIntermediateAgent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntermediateAgentDTO intermediateAgentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update IntermediateAgent : {}, {}", id, intermediateAgentDTO);
        if (intermediateAgentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intermediateAgentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intermediateAgentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        IntermediateAgentDTO result = intermediateAgentService.update(intermediateAgentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, intermediateAgentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /intermediate-agents/:id} : Partial updates given fields of an existing intermediateAgent, field will ignore if it is null
     *
     * @param id the id of the intermediateAgentDTO to save.
     * @param intermediateAgentDTO the intermediateAgentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intermediateAgentDTO,
     * or with status {@code 400 (Bad Request)} if the intermediateAgentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the intermediateAgentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the intermediateAgentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/intermediate-agents/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntermediateAgentDTO> partialUpdateIntermediateAgent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntermediateAgentDTO intermediateAgentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update IntermediateAgent partially : {}, {}", id, intermediateAgentDTO);
        if (intermediateAgentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intermediateAgentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intermediateAgentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IntermediateAgentDTO> result = intermediateAgentService.partialUpdate(intermediateAgentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, intermediateAgentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /intermediate-agents} : get all the intermediateAgents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of intermediateAgents in body.
     */
    @GetMapping("/intermediate-agents")
    public ResponseEntity<List<IntermediateAgentDTO>> getAllIntermediateAgents(
        IntermediateAgentCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get IntermediateAgents by criteria: {}", criteria);
        Page<IntermediateAgentDTO> page = intermediateAgentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /intermediate-agents/count} : count all the intermediateAgents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/intermediate-agents/count")
    public ResponseEntity<Long> countIntermediateAgents(IntermediateAgentCriteria criteria) {
        log.debug("REST request to count IntermediateAgents by criteria: {}", criteria);
        return ResponseEntity.ok().body(intermediateAgentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /intermediate-agents/:id} : get the "id" intermediateAgent.
     *
     * @param id the id of the intermediateAgentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the intermediateAgentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/intermediate-agents/{id}")
    public ResponseEntity<IntermediateAgentDTO> getIntermediateAgent(@PathVariable Long id) {
        log.debug("REST request to get IntermediateAgent : {}", id);
        Optional<IntermediateAgentDTO> intermediateAgentDTO = intermediateAgentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(intermediateAgentDTO);
    }

    /**
     * {@code DELETE  /intermediate-agents/:id} : delete the "id" intermediateAgent.
     *
     * @param id the id of the intermediateAgentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/intermediate-agents/{id}")
    public ResponseEntity<Void> deleteIntermediateAgent(@PathVariable Long id) {
        log.debug("REST request to delete IntermediateAgent : {}", id);
        intermediateAgentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/intermediate-agents?query=:query} : search for the intermediateAgent corresponding
     * to the query.
     *
     * @param query the query of the intermediateAgent search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/intermediate-agents")
    public ResponseEntity<List<IntermediateAgentDTO>> searchIntermediateAgents(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of IntermediateAgents for query {}", query);
        Page<IntermediateAgentDTO> page = intermediateAgentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
