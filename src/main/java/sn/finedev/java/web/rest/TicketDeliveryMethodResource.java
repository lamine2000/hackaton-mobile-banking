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
import sn.finedev.java.repository.TicketDeliveryMethodRepository;
import sn.finedev.java.service.TicketDeliveryMethodQueryService;
import sn.finedev.java.service.TicketDeliveryMethodService;
import sn.finedev.java.service.criteria.TicketDeliveryMethodCriteria;
import sn.finedev.java.service.dto.TicketDeliveryMethodDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.TicketDeliveryMethod}.
 */
@RestController
@RequestMapping("/api")
public class TicketDeliveryMethodResource {

    private final Logger log = LoggerFactory.getLogger(TicketDeliveryMethodResource.class);

    private static final String ENTITY_NAME = "ticketDeliveryMethod";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketDeliveryMethodService ticketDeliveryMethodService;

    private final TicketDeliveryMethodRepository ticketDeliveryMethodRepository;

    private final TicketDeliveryMethodQueryService ticketDeliveryMethodQueryService;

    public TicketDeliveryMethodResource(
        TicketDeliveryMethodService ticketDeliveryMethodService,
        TicketDeliveryMethodRepository ticketDeliveryMethodRepository,
        TicketDeliveryMethodQueryService ticketDeliveryMethodQueryService
    ) {
        this.ticketDeliveryMethodService = ticketDeliveryMethodService;
        this.ticketDeliveryMethodRepository = ticketDeliveryMethodRepository;
        this.ticketDeliveryMethodQueryService = ticketDeliveryMethodQueryService;
    }

    /**
     * {@code POST  /ticket-delivery-methods} : Create a new ticketDeliveryMethod.
     *
     * @param ticketDeliveryMethodDTO the ticketDeliveryMethodDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketDeliveryMethodDTO, or with status {@code 400 (Bad Request)} if the ticketDeliveryMethod has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ticket-delivery-methods")
    public ResponseEntity<TicketDeliveryMethodDTO> createTicketDeliveryMethod(
        @Valid @RequestBody TicketDeliveryMethodDTO ticketDeliveryMethodDTO
    ) throws URISyntaxException {
        log.debug("REST request to save TicketDeliveryMethod : {}", ticketDeliveryMethodDTO);
        if (ticketDeliveryMethodDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticketDeliveryMethod cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TicketDeliveryMethodDTO result = ticketDeliveryMethodService.save(ticketDeliveryMethodDTO);
        return ResponseEntity
            .created(new URI("/api/ticket-delivery-methods/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ticket-delivery-methods/:id} : Updates an existing ticketDeliveryMethod.
     *
     * @param id the id of the ticketDeliveryMethodDTO to save.
     * @param ticketDeliveryMethodDTO the ticketDeliveryMethodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketDeliveryMethodDTO,
     * or with status {@code 400 (Bad Request)} if the ticketDeliveryMethodDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketDeliveryMethodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ticket-delivery-methods/{id}")
    public ResponseEntity<TicketDeliveryMethodDTO> updateTicketDeliveryMethod(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketDeliveryMethodDTO ticketDeliveryMethodDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TicketDeliveryMethod : {}, {}", id, ticketDeliveryMethodDTO);
        if (ticketDeliveryMethodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketDeliveryMethodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketDeliveryMethodRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TicketDeliveryMethodDTO result = ticketDeliveryMethodService.update(ticketDeliveryMethodDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketDeliveryMethodDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ticket-delivery-methods/:id} : Partial updates given fields of an existing ticketDeliveryMethod, field will ignore if it is null
     *
     * @param id the id of the ticketDeliveryMethodDTO to save.
     * @param ticketDeliveryMethodDTO the ticketDeliveryMethodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketDeliveryMethodDTO,
     * or with status {@code 400 (Bad Request)} if the ticketDeliveryMethodDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ticketDeliveryMethodDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketDeliveryMethodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ticket-delivery-methods/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketDeliveryMethodDTO> partialUpdateTicketDeliveryMethod(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketDeliveryMethodDTO ticketDeliveryMethodDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TicketDeliveryMethod partially : {}, {}", id, ticketDeliveryMethodDTO);
        if (ticketDeliveryMethodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketDeliveryMethodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketDeliveryMethodRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketDeliveryMethodDTO> result = ticketDeliveryMethodService.partialUpdate(ticketDeliveryMethodDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketDeliveryMethodDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-delivery-methods} : get all the ticketDeliveryMethods.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketDeliveryMethods in body.
     */
    @GetMapping("/ticket-delivery-methods")
    public ResponseEntity<List<TicketDeliveryMethodDTO>> getAllTicketDeliveryMethods(
        TicketDeliveryMethodCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TicketDeliveryMethods by criteria: {}", criteria);
        Page<TicketDeliveryMethodDTO> page = ticketDeliveryMethodQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ticket-delivery-methods/count} : count all the ticketDeliveryMethods.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ticket-delivery-methods/count")
    public ResponseEntity<Long> countTicketDeliveryMethods(TicketDeliveryMethodCriteria criteria) {
        log.debug("REST request to count TicketDeliveryMethods by criteria: {}", criteria);
        return ResponseEntity.ok().body(ticketDeliveryMethodQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ticket-delivery-methods/:id} : get the "id" ticketDeliveryMethod.
     *
     * @param id the id of the ticketDeliveryMethodDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketDeliveryMethodDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ticket-delivery-methods/{id}")
    public ResponseEntity<TicketDeliveryMethodDTO> getTicketDeliveryMethod(@PathVariable Long id) {
        log.debug("REST request to get TicketDeliveryMethod : {}", id);
        Optional<TicketDeliveryMethodDTO> ticketDeliveryMethodDTO = ticketDeliveryMethodService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketDeliveryMethodDTO);
    }

    /**
     * {@code DELETE  /ticket-delivery-methods/:id} : delete the "id" ticketDeliveryMethod.
     *
     * @param id the id of the ticketDeliveryMethodDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ticket-delivery-methods/{id}")
    public ResponseEntity<Void> deleteTicketDeliveryMethod(@PathVariable Long id) {
        log.debug("REST request to delete TicketDeliveryMethod : {}", id);
        ticketDeliveryMethodService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/ticket-delivery-methods?query=:query} : search for the ticketDeliveryMethod corresponding
     * to the query.
     *
     * @param query the query of the ticketDeliveryMethod search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/ticket-delivery-methods")
    public ResponseEntity<List<TicketDeliveryMethodDTO>> searchTicketDeliveryMethods(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TicketDeliveryMethods for query {}", query);
        Page<TicketDeliveryMethodDTO> page = ticketDeliveryMethodService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
