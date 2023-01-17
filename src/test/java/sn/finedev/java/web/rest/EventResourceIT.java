package sn.finedev.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import sn.finedev.java.domain.enumeration.EventStatus;
import sn.finedev.java.repository.EventRepository;
import sn.finedev.java.repository.search.EventSearchRepository;
import sn.finedev.java.service.criteria.EventCriteria;
import sn.finedev.java.service.dto.EventDTO;
import sn.finedev.java.service.mapper.EventMapper;

/**
 * Integration tests for the {@link EventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_ORGANIZER = "AAAAAAAAAA";
    private static final String UPDATED_ORGANIZER = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final EventStatus DEFAULT_STATUS = EventStatus.EXPIRED;
    private static final EventStatus UPDATED_STATUS = EventStatus.UPCOMING;

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/events";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private EventSearchRepository eventSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventMockMvc;

    private Event event;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity(EntityManager em) {
        Event event = new Event()
            .date(DEFAULT_DATE)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .organizer(DEFAULT_ORGANIZER)
            .expireAt(DEFAULT_EXPIRE_AT)
            .status(DEFAULT_STATUS);
        return event;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity(EntityManager em) {
        Event event = new Event()
            .date(UPDATED_DATE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .organizer(UPDATED_ORGANIZER)
            .expireAt(UPDATED_EXPIRE_AT)
            .status(UPDATED_STATUS);
        return event;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        eventSearchRepository.deleteAll();
        assertThat(eventSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        event = createEntity(em);
    }

    @Test
    @Transactional
    void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testEvent.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvent.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testEvent.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testEvent.getOrganizer()).isEqualTo(DEFAULT_ORGANIZER);
        assertThat(testEvent.getExpireAt()).isEqualTo(DEFAULT_EXPIRE_AT);
        assertThat(testEvent.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createEventWithExistingId() throws Exception {
        // Create the Event with an existing ID
        event.setId(1L);
        EventDTO eventDTO = eventMapper.toDto(event);

        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setTitle(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setCreatedAt(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setCreatedBy(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOrganizerIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setOrganizer(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkExpireAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setExpireAt(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setStatus(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEvents() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].organizer").value(hasItem(DEFAULT_ORGANIZER)))
            .andExpect(jsonPath("$.[*].expireAt").value(hasItem(DEFAULT_EXPIRE_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc
            .perform(get(ENTITY_API_URL_ID, event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.organizer").value(DEFAULT_ORGANIZER))
            .andExpect(jsonPath("$.expireAt").value(DEFAULT_EXPIRE_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getEventsByIdFiltering() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        Long id = event.getId();

        defaultEventShouldBeFound("id.equals=" + id);
        defaultEventShouldNotBeFound("id.notEquals=" + id);

        defaultEventShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.greaterThan=" + id);

        defaultEventShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where date equals to DEFAULT_DATE
        defaultEventShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the eventList where date equals to UPDATED_DATE
        defaultEventShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where date in DEFAULT_DATE or UPDATED_DATE
        defaultEventShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the eventList where date equals to UPDATED_DATE
        defaultEventShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllEventsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where date is not null
        defaultEventShouldBeFound("date.specified=true");

        // Get all the eventList where date is null
        defaultEventShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title equals to DEFAULT_TITLE
        defaultEventShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the eventList where title equals to UPDATED_TITLE
        defaultEventShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultEventShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the eventList where title equals to UPDATED_TITLE
        defaultEventShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title is not null
        defaultEventShouldBeFound("title.specified=true");

        // Get all the eventList where title is null
        defaultEventShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByTitleContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title contains DEFAULT_TITLE
        defaultEventShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the eventList where title contains UPDATED_TITLE
        defaultEventShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where title does not contain DEFAULT_TITLE
        defaultEventShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the eventList where title does not contain UPDATED_TITLE
        defaultEventShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdAt equals to DEFAULT_CREATED_AT
        defaultEventShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the eventList where createdAt equals to UPDATED_CREATED_AT
        defaultEventShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultEventShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the eventList where createdAt equals to UPDATED_CREATED_AT
        defaultEventShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdAt is not null
        defaultEventShouldBeFound("createdAt.specified=true");

        // Get all the eventList where createdAt is null
        defaultEventShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdBy equals to DEFAULT_CREATED_BY
        defaultEventShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the eventList where createdBy equals to UPDATED_CREATED_BY
        defaultEventShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultEventShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the eventList where createdBy equals to UPDATED_CREATED_BY
        defaultEventShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdBy is not null
        defaultEventShouldBeFound("createdBy.specified=true");

        // Get all the eventList where createdBy is null
        defaultEventShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdBy contains DEFAULT_CREATED_BY
        defaultEventShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the eventList where createdBy contains UPDATED_CREATED_BY
        defaultEventShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEventsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where createdBy does not contain DEFAULT_CREATED_BY
        defaultEventShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the eventList where createdBy does not contain UPDATED_CREATED_BY
        defaultEventShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEventsByOrganizerIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where organizer equals to DEFAULT_ORGANIZER
        defaultEventShouldBeFound("organizer.equals=" + DEFAULT_ORGANIZER);

        // Get all the eventList where organizer equals to UPDATED_ORGANIZER
        defaultEventShouldNotBeFound("organizer.equals=" + UPDATED_ORGANIZER);
    }

    @Test
    @Transactional
    void getAllEventsByOrganizerIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where organizer in DEFAULT_ORGANIZER or UPDATED_ORGANIZER
        defaultEventShouldBeFound("organizer.in=" + DEFAULT_ORGANIZER + "," + UPDATED_ORGANIZER);

        // Get all the eventList where organizer equals to UPDATED_ORGANIZER
        defaultEventShouldNotBeFound("organizer.in=" + UPDATED_ORGANIZER);
    }

    @Test
    @Transactional
    void getAllEventsByOrganizerIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where organizer is not null
        defaultEventShouldBeFound("organizer.specified=true");

        // Get all the eventList where organizer is null
        defaultEventShouldNotBeFound("organizer.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByOrganizerContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where organizer contains DEFAULT_ORGANIZER
        defaultEventShouldBeFound("organizer.contains=" + DEFAULT_ORGANIZER);

        // Get all the eventList where organizer contains UPDATED_ORGANIZER
        defaultEventShouldNotBeFound("organizer.contains=" + UPDATED_ORGANIZER);
    }

    @Test
    @Transactional
    void getAllEventsByOrganizerNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where organizer does not contain DEFAULT_ORGANIZER
        defaultEventShouldNotBeFound("organizer.doesNotContain=" + DEFAULT_ORGANIZER);

        // Get all the eventList where organizer does not contain UPDATED_ORGANIZER
        defaultEventShouldBeFound("organizer.doesNotContain=" + UPDATED_ORGANIZER);
    }

    @Test
    @Transactional
    void getAllEventsByExpireAtIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where expireAt equals to DEFAULT_EXPIRE_AT
        defaultEventShouldBeFound("expireAt.equals=" + DEFAULT_EXPIRE_AT);

        // Get all the eventList where expireAt equals to UPDATED_EXPIRE_AT
        defaultEventShouldNotBeFound("expireAt.equals=" + UPDATED_EXPIRE_AT);
    }

    @Test
    @Transactional
    void getAllEventsByExpireAtIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where expireAt in DEFAULT_EXPIRE_AT or UPDATED_EXPIRE_AT
        defaultEventShouldBeFound("expireAt.in=" + DEFAULT_EXPIRE_AT + "," + UPDATED_EXPIRE_AT);

        // Get all the eventList where expireAt equals to UPDATED_EXPIRE_AT
        defaultEventShouldNotBeFound("expireAt.in=" + UPDATED_EXPIRE_AT);
    }

    @Test
    @Transactional
    void getAllEventsByExpireAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where expireAt is not null
        defaultEventShouldBeFound("expireAt.specified=true");

        // Get all the eventList where expireAt is null
        defaultEventShouldNotBeFound("expireAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where status equals to DEFAULT_STATUS
        defaultEventShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the eventList where status equals to UPDATED_STATUS
        defaultEventShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultEventShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the eventList where status equals to UPDATED_STATUS
        defaultEventShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEventsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where status is not null
        defaultEventShouldBeFound("status.specified=true");

        // Get all the eventList where status is null
        defaultEventShouldNotBeFound("status.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventShouldBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].organizer").value(hasItem(DEFAULT_ORGANIZER)))
            .andExpect(jsonPath("$.[*].expireAt").value(hasItem(DEFAULT_EXPIRE_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventShouldNotBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        eventSearchRepository.save(event);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).get();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        updatedEvent
            .date(UPDATED_DATE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .organizer(UPDATED_ORGANIZER)
            .expireAt(UPDATED_EXPIRE_AT)
            .status(UPDATED_STATUS);
        EventDTO eventDTO = eventMapper.toDto(updatedEvent);

        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testEvent.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testEvent.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testEvent.getOrganizer()).isEqualTo(UPDATED_ORGANIZER);
        assertThat(testEvent.getExpireAt()).isEqualTo(UPDATED_EXPIRE_AT);
        assertThat(testEvent.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Event> eventSearchList = IterableUtils.toList(eventSearchRepository.findAll());
                Event testEventSearch = eventSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEventSearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testEventSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testEventSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testEventSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testEventSearch.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
                assertThat(testEventSearch.getOrganizer()).isEqualTo(UPDATED_ORGANIZER);
                assertThat(testEventSearch.getExpireAt()).isEqualTo(UPDATED_EXPIRE_AT);
                assertThat(testEventSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent.title(UPDATED_TITLE).status(UPDATED_STATUS);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testEvent.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvent.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testEvent.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testEvent.getOrganizer()).isEqualTo(DEFAULT_ORGANIZER);
        assertThat(testEvent.getExpireAt()).isEqualTo(DEFAULT_EXPIRE_AT);
        assertThat(testEvent.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent
            .date(UPDATED_DATE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .organizer(UPDATED_ORGANIZER)
            .expireAt(UPDATED_EXPIRE_AT)
            .status(UPDATED_STATUS);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testEvent.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testEvent.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testEvent.getOrganizer()).isEqualTo(UPDATED_ORGANIZER);
        assertThat(testEvent.getExpireAt()).isEqualTo(UPDATED_EXPIRE_AT);
        assertThat(testEvent.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(count.incrementAndGet());

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(eventDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);
        eventRepository.save(event);
        eventSearchRepository.save(event);

        int databaseSizeBeforeDelete = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the event
        restEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, event.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEvent() throws Exception {
        // Initialize the database
        event = eventRepository.saveAndFlush(event);
        eventSearchRepository.save(event);

        // Search the event
        restEventMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].organizer").value(hasItem(DEFAULT_ORGANIZER)))
            .andExpect(jsonPath("$.[*].expireAt").value(hasItem(DEFAULT_EXPIRE_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
