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
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.Notification;
import sn.finedev.java.repository.NotificationRepository;
import sn.finedev.java.repository.search.NotificationSearchRepository;
import sn.finedev.java.service.criteria.NotificationCriteria;
import sn.finedev.java.service.dto.NotificationDTO;
import sn.finedev.java.service.mapper.NotificationMapper;

/**
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SENT_TO = "AAAAAAAAAA";
    private static final String UPDATED_SENT_TO = "BBBBBBBBBB";

    private static final String DEFAULT_SENT_BY = "AAAAAAAAAA";
    private static final String UPDATED_SENT_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/notifications";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationSearchRepository notificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationMockMvc;

    private Notification notification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification()
            .title(DEFAULT_TITLE)
            .message(DEFAULT_MESSAGE)
            .createdAt(DEFAULT_CREATED_AT)
            .sentTo(DEFAULT_SENT_TO)
            .sentBy(DEFAULT_SENT_BY);
        return notification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification()
            .title(UPDATED_TITLE)
            .message(UPDATED_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .sentTo(UPDATED_SENT_TO)
            .sentBy(UPDATED_SENT_BY);
        return notification;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        notificationSearchRepository.deleteAll();
        assertThat(notificationSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        notification = createEntity(em);
    }

    @Test
    @Transactional
    void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNotification.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testNotification.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testNotification.getSentTo()).isEqualTo(DEFAULT_SENT_TO);
        assertThat(testNotification.getSentBy()).isEqualTo(DEFAULT_SENT_BY);
    }

    @Test
    @Transactional
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setTitle(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setMessage(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setCreatedAt(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSentToIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setSentTo(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSentByIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // set the field null
        notification.setSentBy(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllNotifications() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].sentTo").value(hasItem(DEFAULT_SENT_TO)))
            .andExpect(jsonPath("$.[*].sentBy").value(hasItem(DEFAULT_SENT_BY)));
    }

    @Test
    @Transactional
    void getNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get the notification
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.sentTo").value(DEFAULT_SENT_TO))
            .andExpect(jsonPath("$.sentBy").value(DEFAULT_SENT_BY));
    }

    @Test
    @Transactional
    void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        Long id = notification.getId();

        defaultNotificationShouldBeFound("id.equals=" + id);
        defaultNotificationShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where title equals to DEFAULT_TITLE
        defaultNotificationShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the notificationList where title equals to UPDATED_TITLE
        defaultNotificationShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllNotificationsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultNotificationShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the notificationList where title equals to UPDATED_TITLE
        defaultNotificationShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllNotificationsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where title is not null
        defaultNotificationShouldBeFound("title.specified=true");

        // Get all the notificationList where title is null
        defaultNotificationShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByTitleContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where title contains DEFAULT_TITLE
        defaultNotificationShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the notificationList where title contains UPDATED_TITLE
        defaultNotificationShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllNotificationsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where title does not contain DEFAULT_TITLE
        defaultNotificationShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the notificationList where title does not contain UPDATED_TITLE
        defaultNotificationShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message equals to DEFAULT_MESSAGE
        defaultNotificationShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the notificationList where message equals to UPDATED_MESSAGE
        defaultNotificationShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultNotificationShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the notificationList where message equals to UPDATED_MESSAGE
        defaultNotificationShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message is not null
        defaultNotificationShouldBeFound("message.specified=true");

        // Get all the notificationList where message is null
        defaultNotificationShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message contains DEFAULT_MESSAGE
        defaultNotificationShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the notificationList where message contains UPDATED_MESSAGE
        defaultNotificationShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where message does not contain DEFAULT_MESSAGE
        defaultNotificationShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the notificationList where message does not contain UPDATED_MESSAGE
        defaultNotificationShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt equals to DEFAULT_CREATED_AT
        defaultNotificationShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the notificationList where createdAt equals to UPDATED_CREATED_AT
        defaultNotificationShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultNotificationShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the notificationList where createdAt equals to UPDATED_CREATED_AT
        defaultNotificationShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createdAt is not null
        defaultNotificationShouldBeFound("createdAt.specified=true");

        // Get all the notificationList where createdAt is null
        defaultNotificationShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsBySentToIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentTo equals to DEFAULT_SENT_TO
        defaultNotificationShouldBeFound("sentTo.equals=" + DEFAULT_SENT_TO);

        // Get all the notificationList where sentTo equals to UPDATED_SENT_TO
        defaultNotificationShouldNotBeFound("sentTo.equals=" + UPDATED_SENT_TO);
    }

    @Test
    @Transactional
    void getAllNotificationsBySentToIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentTo in DEFAULT_SENT_TO or UPDATED_SENT_TO
        defaultNotificationShouldBeFound("sentTo.in=" + DEFAULT_SENT_TO + "," + UPDATED_SENT_TO);

        // Get all the notificationList where sentTo equals to UPDATED_SENT_TO
        defaultNotificationShouldNotBeFound("sentTo.in=" + UPDATED_SENT_TO);
    }

    @Test
    @Transactional
    void getAllNotificationsBySentToIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentTo is not null
        defaultNotificationShouldBeFound("sentTo.specified=true");

        // Get all the notificationList where sentTo is null
        defaultNotificationShouldNotBeFound("sentTo.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsBySentToContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentTo contains DEFAULT_SENT_TO
        defaultNotificationShouldBeFound("sentTo.contains=" + DEFAULT_SENT_TO);

        // Get all the notificationList where sentTo contains UPDATED_SENT_TO
        defaultNotificationShouldNotBeFound("sentTo.contains=" + UPDATED_SENT_TO);
    }

    @Test
    @Transactional
    void getAllNotificationsBySentToNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentTo does not contain DEFAULT_SENT_TO
        defaultNotificationShouldNotBeFound("sentTo.doesNotContain=" + DEFAULT_SENT_TO);

        // Get all the notificationList where sentTo does not contain UPDATED_SENT_TO
        defaultNotificationShouldBeFound("sentTo.doesNotContain=" + UPDATED_SENT_TO);
    }

    @Test
    @Transactional
    void getAllNotificationsBySentByIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentBy equals to DEFAULT_SENT_BY
        defaultNotificationShouldBeFound("sentBy.equals=" + DEFAULT_SENT_BY);

        // Get all the notificationList where sentBy equals to UPDATED_SENT_BY
        defaultNotificationShouldNotBeFound("sentBy.equals=" + UPDATED_SENT_BY);
    }

    @Test
    @Transactional
    void getAllNotificationsBySentByIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentBy in DEFAULT_SENT_BY or UPDATED_SENT_BY
        defaultNotificationShouldBeFound("sentBy.in=" + DEFAULT_SENT_BY + "," + UPDATED_SENT_BY);

        // Get all the notificationList where sentBy equals to UPDATED_SENT_BY
        defaultNotificationShouldNotBeFound("sentBy.in=" + UPDATED_SENT_BY);
    }

    @Test
    @Transactional
    void getAllNotificationsBySentByIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentBy is not null
        defaultNotificationShouldBeFound("sentBy.specified=true");

        // Get all the notificationList where sentBy is null
        defaultNotificationShouldNotBeFound("sentBy.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsBySentByContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentBy contains DEFAULT_SENT_BY
        defaultNotificationShouldBeFound("sentBy.contains=" + DEFAULT_SENT_BY);

        // Get all the notificationList where sentBy contains UPDATED_SENT_BY
        defaultNotificationShouldNotBeFound("sentBy.contains=" + UPDATED_SENT_BY);
    }

    @Test
    @Transactional
    void getAllNotificationsBySentByNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where sentBy does not contain DEFAULT_SENT_BY
        defaultNotificationShouldNotBeFound("sentBy.doesNotContain=" + DEFAULT_SENT_BY);

        // Get all the notificationList where sentBy does not contain UPDATED_SENT_BY
        defaultNotificationShouldBeFound("sentBy.doesNotContain=" + UPDATED_SENT_BY);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].sentTo").value(hasItem(DEFAULT_SENT_TO)))
            .andExpect(jsonPath("$.[*].sentBy").value(hasItem(DEFAULT_SENT_BY)));

        // Check, that the count call also returns 1
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationShouldNotBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notificationSearchRepository.save(notification);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).get();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification
            .title(UPDATED_TITLE)
            .message(UPDATED_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .sentTo(UPDATED_SENT_TO)
            .sentBy(UPDATED_SENT_BY);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNotification.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testNotification.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testNotification.getSentTo()).isEqualTo(UPDATED_SENT_TO);
        assertThat(testNotification.getSentBy()).isEqualTo(UPDATED_SENT_BY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Notification> notificationSearchList = IterableUtils.toList(notificationSearchRepository.findAll());
                Notification testNotificationSearch = notificationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testNotificationSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testNotificationSearch.getMessage()).isEqualTo(UPDATED_MESSAGE);
                assertThat(testNotificationSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testNotificationSearch.getSentTo()).isEqualTo(UPDATED_SENT_TO);
                assertThat(testNotificationSearch.getSentBy()).isEqualTo(UPDATED_SENT_BY);
            });
    }

    @Test
    @Transactional
    void putNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification.createdAt(UPDATED_CREATED_AT).sentTo(UPDATED_SENT_TO).sentBy(UPDATED_SENT_BY);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNotification.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testNotification.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testNotification.getSentTo()).isEqualTo(UPDATED_SENT_TO);
        assertThat(testNotification.getSentBy()).isEqualTo(UPDATED_SENT_BY);
    }

    @Test
    @Transactional
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .title(UPDATED_TITLE)
            .message(UPDATED_MESSAGE)
            .createdAt(UPDATED_CREATED_AT)
            .sentTo(UPDATED_SENT_TO)
            .sentBy(UPDATED_SENT_BY);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNotification.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testNotification.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testNotification.getSentTo()).isEqualTo(UPDATED_SENT_TO);
        assertThat(testNotification.getSentBy()).isEqualTo(UPDATED_SENT_BY);
    }

    @Test
    @Transactional
    void patchNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);
        notificationRepository.save(notification);
        notificationSearchRepository.save(notification);

        int databaseSizeBeforeDelete = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notification
        restNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, notification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchNotification() throws Exception {
        // Initialize the database
        notification = notificationRepository.saveAndFlush(notification);
        notificationSearchRepository.save(notification);

        // Search the notification
        restNotificationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].sentTo").value(hasItem(DEFAULT_SENT_TO)))
            .andExpect(jsonPath("$.[*].sentBy").value(hasItem(DEFAULT_SENT_BY)));
    }
}
