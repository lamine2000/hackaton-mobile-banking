package sn.finedev.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.Event;
import sn.finedev.java.domain.Payment;
import sn.finedev.java.domain.Ticket;
import sn.finedev.java.domain.TicketDelivery;
import sn.finedev.java.domain.enumeration.TicketStatus;
import sn.finedev.java.repository.TicketRepository;
import sn.finedev.java.repository.search.TicketSearchRepository;
import sn.finedev.java.service.criteria.TicketCriteria;
import sn.finedev.java.service.dto.TicketDTO;
import sn.finedev.java.service.mapper.TicketMapper;

/**
 * Integration tests for the {@link TicketResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_DATA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DATA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DATA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DATA_CONTENT_TYPE = "image/png";

    private static final Double DEFAULT_PRICE_PER_UNIT = 1D;
    private static final Double UPDATED_PRICE_PER_UNIT = 2D;
    private static final Double SMALLER_PRICE_PER_UNIT = 1D - 1D;

    private static final Double DEFAULT_FINAL_AGENT_COMMISSION = 1D;
    private static final Double UPDATED_FINAL_AGENT_COMMISSION = 2D;
    private static final Double SMALLER_FINAL_AGENT_COMMISSION = 1D - 1D;

    private static final TicketStatus DEFAULT_STATUS = TicketStatus.SOLD;
    private static final TicketStatus UPDATED_STATUS = TicketStatus.AVAILABLE;

    private static final String ENTITY_API_URL = "/api/tickets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tickets";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketSearchRepository ticketSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .code(DEFAULT_CODE)
            .data(DEFAULT_DATA)
            .dataContentType(DEFAULT_DATA_CONTENT_TYPE)
            .pricePerUnit(DEFAULT_PRICE_PER_UNIT)
            .finalAgentCommission(DEFAULT_FINAL_AGENT_COMMISSION)
            .status(DEFAULT_STATUS);
        return ticket;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createUpdatedEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .code(UPDATED_CODE)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .finalAgentCommission(UPDATED_FINAL_AGENT_COMMISSION)
            .status(UPDATED_STATUS);
        return ticket;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        ticketSearchRepository.deleteAll();
        assertThat(ticketSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        ticket = createEntity(em);
    }

    @Test
    @Transactional
    void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);
        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isCreated());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTicket.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testTicket.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
        assertThat(testTicket.getPricePerUnit()).isEqualTo(DEFAULT_PRICE_PER_UNIT);
        assertThat(testTicket.getFinalAgentCommission()).isEqualTo(DEFAULT_FINAL_AGENT_COMMISSION);
        assertThat(testTicket.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createTicketWithExistingId() throws Exception {
        // Create the Ticket with an existing ID
        ticket.setId(1L);
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        int databaseSizeBeforeCreate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPricePerUnitIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setPricePerUnit(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        // set the field null
        ticket.setStatus(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTickets() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(DEFAULT_PRICE_PER_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].finalAgentCommission").value(hasItem(DEFAULT_FINAL_AGENT_COMMISSION.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc
            .perform(get(ENTITY_API_URL_ID, ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.dataContentType").value(DEFAULT_DATA_CONTENT_TYPE))
            .andExpect(jsonPath("$.data").value(Base64Utils.encodeToString(DEFAULT_DATA)))
            .andExpect(jsonPath("$.pricePerUnit").value(DEFAULT_PRICE_PER_UNIT.doubleValue()))
            .andExpect(jsonPath("$.finalAgentCommission").value(DEFAULT_FINAL_AGENT_COMMISSION.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getTicketsByIdFiltering() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        Long id = ticket.getId();

        defaultTicketShouldBeFound("id.equals=" + id);
        defaultTicketShouldNotBeFound("id.notEquals=" + id);

        defaultTicketShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTicketShouldNotBeFound("id.greaterThan=" + id);

        defaultTicketShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTicketShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code equals to DEFAULT_CODE
        defaultTicketShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the ticketList where code equals to UPDATED_CODE
        defaultTicketShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code in DEFAULT_CODE or UPDATED_CODE
        defaultTicketShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the ticketList where code equals to UPDATED_CODE
        defaultTicketShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code is not null
        defaultTicketShouldBeFound("code.specified=true");

        // Get all the ticketList where code is null
        defaultTicketShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByCodeContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code contains DEFAULT_CODE
        defaultTicketShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the ticketList where code contains UPDATED_CODE
        defaultTicketShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where code does not contain DEFAULT_CODE
        defaultTicketShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the ticketList where code does not contain UPDATED_CODE
        defaultTicketShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTicketsByPricePerUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where pricePerUnit equals to DEFAULT_PRICE_PER_UNIT
        defaultTicketShouldBeFound("pricePerUnit.equals=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the ticketList where pricePerUnit equals to UPDATED_PRICE_PER_UNIT
        defaultTicketShouldNotBeFound("pricePerUnit.equals=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllTicketsByPricePerUnitIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where pricePerUnit in DEFAULT_PRICE_PER_UNIT or UPDATED_PRICE_PER_UNIT
        defaultTicketShouldBeFound("pricePerUnit.in=" + DEFAULT_PRICE_PER_UNIT + "," + UPDATED_PRICE_PER_UNIT);

        // Get all the ticketList where pricePerUnit equals to UPDATED_PRICE_PER_UNIT
        defaultTicketShouldNotBeFound("pricePerUnit.in=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllTicketsByPricePerUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where pricePerUnit is not null
        defaultTicketShouldBeFound("pricePerUnit.specified=true");

        // Get all the ticketList where pricePerUnit is null
        defaultTicketShouldNotBeFound("pricePerUnit.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByPricePerUnitIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where pricePerUnit is greater than or equal to DEFAULT_PRICE_PER_UNIT
        defaultTicketShouldBeFound("pricePerUnit.greaterThanOrEqual=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the ticketList where pricePerUnit is greater than or equal to UPDATED_PRICE_PER_UNIT
        defaultTicketShouldNotBeFound("pricePerUnit.greaterThanOrEqual=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllTicketsByPricePerUnitIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where pricePerUnit is less than or equal to DEFAULT_PRICE_PER_UNIT
        defaultTicketShouldBeFound("pricePerUnit.lessThanOrEqual=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the ticketList where pricePerUnit is less than or equal to SMALLER_PRICE_PER_UNIT
        defaultTicketShouldNotBeFound("pricePerUnit.lessThanOrEqual=" + SMALLER_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllTicketsByPricePerUnitIsLessThanSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where pricePerUnit is less than DEFAULT_PRICE_PER_UNIT
        defaultTicketShouldNotBeFound("pricePerUnit.lessThan=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the ticketList where pricePerUnit is less than UPDATED_PRICE_PER_UNIT
        defaultTicketShouldBeFound("pricePerUnit.lessThan=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllTicketsByPricePerUnitIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where pricePerUnit is greater than DEFAULT_PRICE_PER_UNIT
        defaultTicketShouldNotBeFound("pricePerUnit.greaterThan=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the ticketList where pricePerUnit is greater than SMALLER_PRICE_PER_UNIT
        defaultTicketShouldBeFound("pricePerUnit.greaterThan=" + SMALLER_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllTicketsByFinalAgentCommissionIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where finalAgentCommission equals to DEFAULT_FINAL_AGENT_COMMISSION
        defaultTicketShouldBeFound("finalAgentCommission.equals=" + DEFAULT_FINAL_AGENT_COMMISSION);

        // Get all the ticketList where finalAgentCommission equals to UPDATED_FINAL_AGENT_COMMISSION
        defaultTicketShouldNotBeFound("finalAgentCommission.equals=" + UPDATED_FINAL_AGENT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllTicketsByFinalAgentCommissionIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where finalAgentCommission in DEFAULT_FINAL_AGENT_COMMISSION or UPDATED_FINAL_AGENT_COMMISSION
        defaultTicketShouldBeFound("finalAgentCommission.in=" + DEFAULT_FINAL_AGENT_COMMISSION + "," + UPDATED_FINAL_AGENT_COMMISSION);

        // Get all the ticketList where finalAgentCommission equals to UPDATED_FINAL_AGENT_COMMISSION
        defaultTicketShouldNotBeFound("finalAgentCommission.in=" + UPDATED_FINAL_AGENT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllTicketsByFinalAgentCommissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where finalAgentCommission is not null
        defaultTicketShouldBeFound("finalAgentCommission.specified=true");

        // Get all the ticketList where finalAgentCommission is null
        defaultTicketShouldNotBeFound("finalAgentCommission.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByFinalAgentCommissionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where finalAgentCommission is greater than or equal to DEFAULT_FINAL_AGENT_COMMISSION
        defaultTicketShouldBeFound("finalAgentCommission.greaterThanOrEqual=" + DEFAULT_FINAL_AGENT_COMMISSION);

        // Get all the ticketList where finalAgentCommission is greater than or equal to UPDATED_FINAL_AGENT_COMMISSION
        defaultTicketShouldNotBeFound("finalAgentCommission.greaterThanOrEqual=" + UPDATED_FINAL_AGENT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllTicketsByFinalAgentCommissionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where finalAgentCommission is less than or equal to DEFAULT_FINAL_AGENT_COMMISSION
        defaultTicketShouldBeFound("finalAgentCommission.lessThanOrEqual=" + DEFAULT_FINAL_AGENT_COMMISSION);

        // Get all the ticketList where finalAgentCommission is less than or equal to SMALLER_FINAL_AGENT_COMMISSION
        defaultTicketShouldNotBeFound("finalAgentCommission.lessThanOrEqual=" + SMALLER_FINAL_AGENT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllTicketsByFinalAgentCommissionIsLessThanSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where finalAgentCommission is less than DEFAULT_FINAL_AGENT_COMMISSION
        defaultTicketShouldNotBeFound("finalAgentCommission.lessThan=" + DEFAULT_FINAL_AGENT_COMMISSION);

        // Get all the ticketList where finalAgentCommission is less than UPDATED_FINAL_AGENT_COMMISSION
        defaultTicketShouldBeFound("finalAgentCommission.lessThan=" + UPDATED_FINAL_AGENT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllTicketsByFinalAgentCommissionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where finalAgentCommission is greater than DEFAULT_FINAL_AGENT_COMMISSION
        defaultTicketShouldNotBeFound("finalAgentCommission.greaterThan=" + DEFAULT_FINAL_AGENT_COMMISSION);

        // Get all the ticketList where finalAgentCommission is greater than SMALLER_FINAL_AGENT_COMMISSION
        defaultTicketShouldBeFound("finalAgentCommission.greaterThan=" + SMALLER_FINAL_AGENT_COMMISSION);
    }

    @Test
    @Transactional
    void getAllTicketsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status equals to DEFAULT_STATUS
        defaultTicketShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the ticketList where status equals to UPDATED_STATUS
        defaultTicketShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTicketsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultTicketShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the ticketList where status equals to UPDATED_STATUS
        defaultTicketShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTicketsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where status is not null
        defaultTicketShouldBeFound("status.specified=true");

        // Get all the ticketList where status is null
        defaultTicketShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketsByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            ticketRepository.saveAndFlush(ticket);
            event = EventResourceIT.createEntity(em);
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        ticket.setEvent(event);
        ticketRepository.saveAndFlush(ticket);
        Long eventId = event.getId();

        // Get all the ticketList where event equals to eventId
        defaultTicketShouldBeFound("eventId.equals=" + eventId);

        // Get all the ticketList where event equals to (eventId + 1)
        defaultTicketShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    @Test
    @Transactional
    void getAllTicketsByPaymentIsEqualToSomething() throws Exception {
        Payment payment;
        if (TestUtil.findAll(em, Payment.class).isEmpty()) {
            ticketRepository.saveAndFlush(ticket);
            payment = PaymentResourceIT.createEntity(em);
        } else {
            payment = TestUtil.findAll(em, Payment.class).get(0);
        }
        em.persist(payment);
        em.flush();
        ticket.setPayment(payment);
        ticketRepository.saveAndFlush(ticket);
        Long paymentId = payment.getId();

        // Get all the ticketList where payment equals to paymentId
        defaultTicketShouldBeFound("paymentId.equals=" + paymentId);

        // Get all the ticketList where payment equals to (paymentId + 1)
        defaultTicketShouldNotBeFound("paymentId.equals=" + (paymentId + 1));
    }

    @Test
    @Transactional
    void getAllTicketsByTicketDeliveryIsEqualToSomething() throws Exception {
        TicketDelivery ticketDelivery;
        if (TestUtil.findAll(em, TicketDelivery.class).isEmpty()) {
            ticketRepository.saveAndFlush(ticket);
            ticketDelivery = TicketDeliveryResourceIT.createEntity(em);
        } else {
            ticketDelivery = TestUtil.findAll(em, TicketDelivery.class).get(0);
        }
        em.persist(ticketDelivery);
        em.flush();
        ticket.setTicketDelivery(ticketDelivery);
        ticketRepository.saveAndFlush(ticket);
        Long ticketDeliveryId = ticketDelivery.getId();

        // Get all the ticketList where ticketDelivery equals to ticketDeliveryId
        defaultTicketShouldBeFound("ticketDeliveryId.equals=" + ticketDeliveryId);

        // Get all the ticketList where ticketDelivery equals to (ticketDeliveryId + 1)
        defaultTicketShouldNotBeFound("ticketDeliveryId.equals=" + (ticketDeliveryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketShouldBeFound(String filter) throws Exception {
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(DEFAULT_PRICE_PER_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].finalAgentCommission").value(hasItem(DEFAULT_FINAL_AGENT_COMMISSION.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketShouldNotBeFound(String filter) throws Exception {
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();
        ticketSearchRepository.save(ticket);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());

        // Update the ticket
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).get();
        // Disconnect from session so that the updates on updatedTicket are not directly saved in db
        em.detach(updatedTicket);
        updatedTicket
            .code(UPDATED_CODE)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .finalAgentCommission(UPDATED_FINAL_AGENT_COMMISSION)
            .status(UPDATED_STATUS);
        TicketDTO ticketDTO = ticketMapper.toDto(updatedTicket);

        restTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDTO))
            )
            .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTicket.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testTicket.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
        assertThat(testTicket.getPricePerUnit()).isEqualTo(UPDATED_PRICE_PER_UNIT);
        assertThat(testTicket.getFinalAgentCommission()).isEqualTo(UPDATED_FINAL_AGENT_COMMISSION);
        assertThat(testTicket.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Ticket> ticketSearchList = IterableUtils.toList(ticketSearchRepository.findAll());
                Ticket testTicketSearch = ticketSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTicketSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testTicketSearch.getData()).isEqualTo(UPDATED_DATA);
                assertThat(testTicketSearch.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
                assertThat(testTicketSearch.getPricePerUnit()).isEqualTo(UPDATED_PRICE_PER_UNIT);
                assertThat(testTicketSearch.getFinalAgentCommission()).isEqualTo(UPDATED_FINAL_AGENT_COMMISSION);
                assertThat(testTicketSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket.pricePerUnit(UPDATED_PRICE_PER_UNIT);

        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicket))
            )
            .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTicket.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testTicket.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
        assertThat(testTicket.getPricePerUnit()).isEqualTo(UPDATED_PRICE_PER_UNIT);
        assertThat(testTicket.getFinalAgentCommission()).isEqualTo(DEFAULT_FINAL_AGENT_COMMISSION);
        assertThat(testTicket.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket
            .code(UPDATED_CODE)
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .finalAgentCommission(UPDATED_FINAL_AGENT_COMMISSION)
            .status(UPDATED_STATUS);

        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicket))
            )
            .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTicket.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testTicket.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
        assertThat(testTicket.getPricePerUnit()).isEqualTo(UPDATED_PRICE_PER_UNIT);
        assertThat(testTicket.getFinalAgentCommission()).isEqualTo(UPDATED_FINAL_AGENT_COMMISSION);
        assertThat(testTicket.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ticketDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        ticketRepository.save(ticket);
        ticketSearchRepository.save(ticket);

        int databaseSizeBeforeDelete = ticketRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticket
        restTicketMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticket.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicket() throws Exception {
        // Initialize the database
        ticket = ticketRepository.saveAndFlush(ticket);
        ticketSearchRepository.save(ticket);

        // Search the ticket
        restTicketMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(DEFAULT_PRICE_PER_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].finalAgentCommission").value(hasItem(DEFAULT_FINAL_AGENT_COMMISSION.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
