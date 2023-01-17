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
import sn.finedev.java.repository.TransacRepository;
import sn.finedev.java.service.TransacQueryService;
import sn.finedev.java.service.TransacService;
import sn.finedev.java.service.criteria.TransacCriteria;
import sn.finedev.java.service.dto.TransacDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.Transac}.
 */
@RestController
@RequestMapping("/api")
public class TransacResource {

    private final Logger log = LoggerFactory.getLogger(TransacResource.class);

    private static final String ENTITY_NAME = "transac";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransacService transacService;

    private final TransacRepository transacRepository;

    private final TransacQueryService transacQueryService;

    public TransacResource(TransacService transacService, TransacRepository transacRepository, TransacQueryService transacQueryService) {
        this.transacService = transacService;
        this.transacRepository = transacRepository;
        this.transacQueryService = transacQueryService;
    }

    /**
     * {@code POST  /transacs} : Create a new transac.
     *
     * @param transacDTO the transacDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transacDTO, or with status {@code 400 (Bad Request)} if the transac has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transacs")
    public ResponseEntity<TransacDTO> createTransac(@Valid @RequestBody TransacDTO transacDTO) throws URISyntaxException {
        log.debug("REST request to save Transac : {}", transacDTO);
        if (transacDTO.getId() != null) {
            throw new BadRequestAlertException("A new transac cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransacDTO result = transacService.save(transacDTO);
        return ResponseEntity
            .created(new URI("/api/transacs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transacs/:id} : Updates an existing transac.
     *
     * @param id the id of the transacDTO to save.
     * @param transacDTO the transacDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transacDTO,
     * or with status {@code 400 (Bad Request)} if the transacDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transacDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transacs/{id}")
    public ResponseEntity<TransacDTO> updateTransac(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransacDTO transacDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Transac : {}, {}", id, transacDTO);
        if (transacDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transacDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transacRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransacDTO result = transacService.update(transacDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transacDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transacs/:id} : Partial updates given fields of an existing transac, field will ignore if it is null
     *
     * @param id the id of the transacDTO to save.
     * @param transacDTO the transacDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transacDTO,
     * or with status {@code 400 (Bad Request)} if the transacDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transacDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transacDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transacs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransacDTO> partialUpdateTransac(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransacDTO transacDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Transac partially : {}, {}", id, transacDTO);
        if (transacDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transacDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transacRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransacDTO> result = transacService.partialUpdate(transacDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transacDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transacs} : get all the transacs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transacs in body.
     */
    @GetMapping("/transacs")
    public ResponseEntity<List<TransacDTO>> getAllTransacs(
        TransacCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Transacs by criteria: {}", criteria);
        Page<TransacDTO> page = transacQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transacs/count} : count all the transacs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transacs/count")
    public ResponseEntity<Long> countTransacs(TransacCriteria criteria) {
        log.debug("REST request to count Transacs by criteria: {}", criteria);
        return ResponseEntity.ok().body(transacQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transacs/:id} : get the "id" transac.
     *
     * @param id the id of the transacDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transacDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transacs/{id}")
    public ResponseEntity<TransacDTO> getTransac(@PathVariable Long id) {
        log.debug("REST request to get Transac : {}", id);
        Optional<TransacDTO> transacDTO = transacService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transacDTO);
    }

    /**
     * {@code DELETE  /transacs/:id} : delete the "id" transac.
     *
     * @param id the id of the transacDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transacs/{id}")
    public ResponseEntity<Void> deleteTransac(@PathVariable Long id) {
        log.debug("REST request to delete Transac : {}", id);
        transacService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transacs?query=:query} : search for the transac corresponding
     * to the query.
     *
     * @param query the query of the transac search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transacs")
    public ResponseEntity<List<TransacDTO>> searchTransacs(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Transacs for query {}", query);
        Page<TransacDTO> page = transacService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
