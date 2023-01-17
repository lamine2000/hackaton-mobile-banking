package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.TicketDeliveryMethod;
import sn.finedev.java.repository.TicketDeliveryMethodRepository;
import sn.finedev.java.repository.search.TicketDeliveryMethodSearchRepository;
import sn.finedev.java.service.TicketDeliveryMethodService;
import sn.finedev.java.service.dto.TicketDeliveryMethodDTO;
import sn.finedev.java.service.mapper.TicketDeliveryMethodMapper;

/**
 * Service Implementation for managing {@link TicketDeliveryMethod}.
 */
@Service
@Transactional
public class TicketDeliveryMethodServiceImpl implements TicketDeliveryMethodService {

    private final Logger log = LoggerFactory.getLogger(TicketDeliveryMethodServiceImpl.class);

    private final TicketDeliveryMethodRepository ticketDeliveryMethodRepository;

    private final TicketDeliveryMethodMapper ticketDeliveryMethodMapper;

    private final TicketDeliveryMethodSearchRepository ticketDeliveryMethodSearchRepository;

    public TicketDeliveryMethodServiceImpl(
        TicketDeliveryMethodRepository ticketDeliveryMethodRepository,
        TicketDeliveryMethodMapper ticketDeliveryMethodMapper,
        TicketDeliveryMethodSearchRepository ticketDeliveryMethodSearchRepository
    ) {
        this.ticketDeliveryMethodRepository = ticketDeliveryMethodRepository;
        this.ticketDeliveryMethodMapper = ticketDeliveryMethodMapper;
        this.ticketDeliveryMethodSearchRepository = ticketDeliveryMethodSearchRepository;
    }

    @Override
    public TicketDeliveryMethodDTO save(TicketDeliveryMethodDTO ticketDeliveryMethodDTO) {
        log.debug("Request to save TicketDeliveryMethod : {}", ticketDeliveryMethodDTO);
        TicketDeliveryMethod ticketDeliveryMethod = ticketDeliveryMethodMapper.toEntity(ticketDeliveryMethodDTO);
        ticketDeliveryMethod = ticketDeliveryMethodRepository.save(ticketDeliveryMethod);
        TicketDeliveryMethodDTO result = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);
        ticketDeliveryMethodSearchRepository.index(ticketDeliveryMethod);
        return result;
    }

    @Override
    public TicketDeliveryMethodDTO update(TicketDeliveryMethodDTO ticketDeliveryMethodDTO) {
        log.debug("Request to update TicketDeliveryMethod : {}", ticketDeliveryMethodDTO);
        TicketDeliveryMethod ticketDeliveryMethod = ticketDeliveryMethodMapper.toEntity(ticketDeliveryMethodDTO);
        ticketDeliveryMethod = ticketDeliveryMethodRepository.save(ticketDeliveryMethod);
        TicketDeliveryMethodDTO result = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);
        ticketDeliveryMethodSearchRepository.index(ticketDeliveryMethod);
        return result;
    }

    @Override
    public Optional<TicketDeliveryMethodDTO> partialUpdate(TicketDeliveryMethodDTO ticketDeliveryMethodDTO) {
        log.debug("Request to partially update TicketDeliveryMethod : {}", ticketDeliveryMethodDTO);

        return ticketDeliveryMethodRepository
            .findById(ticketDeliveryMethodDTO.getId())
            .map(existingTicketDeliveryMethod -> {
                ticketDeliveryMethodMapper.partialUpdate(existingTicketDeliveryMethod, ticketDeliveryMethodDTO);

                return existingTicketDeliveryMethod;
            })
            .map(ticketDeliveryMethodRepository::save)
            .map(savedTicketDeliveryMethod -> {
                ticketDeliveryMethodSearchRepository.save(savedTicketDeliveryMethod);

                return savedTicketDeliveryMethod;
            })
            .map(ticketDeliveryMethodMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDeliveryMethodDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TicketDeliveryMethods");
        return ticketDeliveryMethodRepository.findAll(pageable).map(ticketDeliveryMethodMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDeliveryMethodDTO> findOne(Long id) {
        log.debug("Request to get TicketDeliveryMethod : {}", id);
        return ticketDeliveryMethodRepository.findById(id).map(ticketDeliveryMethodMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TicketDeliveryMethod : {}", id);
        ticketDeliveryMethodRepository.deleteById(id);
        ticketDeliveryMethodSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDeliveryMethodDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TicketDeliveryMethods for query {}", query);
        return ticketDeliveryMethodSearchRepository.search(query, pageable).map(ticketDeliveryMethodMapper::toDto);
    }
}
