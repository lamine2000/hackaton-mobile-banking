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
import sn.finedev.java.repository.TicketDeliveryRepository;
import sn.finedev.java.service.TicketDeliveryQueryService;
import sn.finedev.java.service.TicketDeliveryService;
import sn.finedev.java.service.criteria.TicketDeliveryCriteria;
import sn.finedev.java.service.dto.TicketDeliveryDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.TicketDelivery}.
 */
@RestController
@RequestMapping("/api")
public class TicketDeliveryResource {

    private final Logger log = LoggerFactory.getLogger(TicketDeliveryResource.class);

    private static final String ENTITY_NAME = "ticketDelivery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TicketDeliveryService ticketDeliveryService;

    private final TicketDeliveryRepository ticketDeliveryRepository;

    private final TicketDeliveryQueryService ticketDeliveryQueryService;

    public TicketDeliveryResource(
        TicketDeliveryService ticketDeliveryService,
        TicketDeliveryRepository ticketDeliveryRepository,
        TicketDeliveryQueryService ticketDeliveryQueryService
    ) {
        this.ticketDeliveryService = ticketDeliveryService;
        this.ticketDeliveryRepository = ticketDeliveryRepository;
        this.ticketDeliveryQueryService = ticketDeliveryQueryService;
    }

    /**
     * {@code POST  /ticket-deliveries} : Create a new ticketDelivery.
     *
     * @param ticketDeliveryDTO the ticketDeliveryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketDeliveryDTO, or with status {@code 400 (Bad Request)} if the ticketDelivery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ticket-deliveries")
    public ResponseEntity<TicketDeliveryDTO> createTicketDelivery(@Valid @RequestBody TicketDeliveryDTO ticketDeliveryDTO)
        throws URISyntaxException {
        log.debug("REST request to save TicketDelivery : {}", ticketDeliveryDTO);
        if (ticketDeliveryDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticketDelivery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TicketDeliveryDTO result = ticketDeliveryService.save(ticketDeliveryDTO);
        return ResponseEntity
            .created(new URI("/api/ticket-deliveries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ticket-deliveries/:id} : Updates an existing ticketDelivery.
     *
     * @param id the id of the ticketDeliveryDTO to save.
     * @param ticketDeliveryDTO the ticketDeliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketDeliveryDTO,
     * or with status {@code 400 (Bad Request)} if the ticketDeliveryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketDeliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ticket-deliveries/{id}")
    public ResponseEntity<TicketDeliveryDTO> updateTicketDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketDeliveryDTO ticketDeliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TicketDelivery : {}, {}", id, ticketDeliveryDTO);
        if (ticketDeliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketDeliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketDeliveryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TicketDeliveryDTO result = ticketDeliveryService.update(ticketDeliveryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketDeliveryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ticket-deliveries/:id} : Partial updates given fields of an existing ticketDelivery, field will ignore if it is null
     *
     * @param id the id of the ticketDeliveryDTO to save.
     * @param ticketDeliveryDTO the ticketDeliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketDeliveryDTO,
     * or with status {@code 400 (Bad Request)} if the ticketDeliveryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ticketDeliveryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketDeliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ticket-deliveries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketDeliveryDTO> partialUpdateTicketDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketDeliveryDTO ticketDeliveryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TicketDelivery partially : {}, {}", id, ticketDeliveryDTO);
        if (ticketDeliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketDeliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketDeliveryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketDeliveryDTO> result = ticketDeliveryService.partialUpdate(ticketDeliveryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketDeliveryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-deliveries} : get all the ticketDeliveries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ticketDeliveries in body.
     */
    @GetMapping("/ticket-deliveries")
    public ResponseEntity<List<TicketDeliveryDTO>> getAllTicketDeliveries(
        TicketDeliveryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TicketDeliveries by criteria: {}", criteria);
        Page<TicketDeliveryDTO> page = ticketDeliveryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ticket-deliveries/count} : count all the ticketDeliveries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ticket-deliveries/count")
    public ResponseEntity<Long> countTicketDeliveries(TicketDeliveryCriteria criteria) {
        log.debug("REST request to count TicketDeliveries by criteria: {}", criteria);
        return ResponseEntity.ok().body(ticketDeliveryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ticket-deliveries/:id} : get the "id" ticketDelivery.
     *
     * @param id the id of the ticketDeliveryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketDeliveryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ticket-deliveries/{id}")
    public ResponseEntity<TicketDeliveryDTO> getTicketDelivery(@PathVariable Long id) {
        log.debug("REST request to get TicketDelivery : {}", id);
        Optional<TicketDeliveryDTO> ticketDeliveryDTO = ticketDeliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketDeliveryDTO);
    }

    /**
     * {@code DELETE  /ticket-deliveries/:id} : delete the "id" ticketDelivery.
     *
     * @param id the id of the ticketDeliveryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ticket-deliveries/{id}")
    public ResponseEntity<Void> deleteTicketDelivery(@PathVariable Long id) {
        log.debug("REST request to delete TicketDelivery : {}", id);
        ticketDeliveryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/ticket-deliveries?query=:query} : search for the ticketDelivery corresponding
     * to the query.
     *
     * @param query the query of the ticketDelivery search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/ticket-deliveries")
    public ResponseEntity<List<TicketDeliveryDTO>> searchTicketDeliveries(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TicketDeliveries for query {}", query);
        Page<TicketDeliveryDTO> page = ticketDeliveryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
