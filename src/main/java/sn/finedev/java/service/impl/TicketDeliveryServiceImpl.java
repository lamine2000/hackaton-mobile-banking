package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.TicketDelivery;
import sn.finedev.java.repository.TicketDeliveryRepository;
import sn.finedev.java.repository.search.TicketDeliverySearchRepository;
import sn.finedev.java.service.TicketDeliveryService;
import sn.finedev.java.service.dto.TicketDeliveryDTO;
import sn.finedev.java.service.mapper.TicketDeliveryMapper;

/**
 * Service Implementation for managing {@link TicketDelivery}.
 */
@Service
@Transactional
public class TicketDeliveryServiceImpl implements TicketDeliveryService {

    private final Logger log = LoggerFactory.getLogger(TicketDeliveryServiceImpl.class);

    private final TicketDeliveryRepository ticketDeliveryRepository;

    private final TicketDeliveryMapper ticketDeliveryMapper;

    private final TicketDeliverySearchRepository ticketDeliverySearchRepository;

    public TicketDeliveryServiceImpl(
        TicketDeliveryRepository ticketDeliveryRepository,
        TicketDeliveryMapper ticketDeliveryMapper,
        TicketDeliverySearchRepository ticketDeliverySearchRepository
    ) {
        this.ticketDeliveryRepository = ticketDeliveryRepository;
        this.ticketDeliveryMapper = ticketDeliveryMapper;
        this.ticketDeliverySearchRepository = ticketDeliverySearchRepository;
    }

    @Override
    public TicketDeliveryDTO save(TicketDeliveryDTO ticketDeliveryDTO) {
        log.debug("Request to save TicketDelivery : {}", ticketDeliveryDTO);
        TicketDelivery ticketDelivery = ticketDeliveryMapper.toEntity(ticketDeliveryDTO);
        ticketDelivery = ticketDeliveryRepository.save(ticketDelivery);
        TicketDeliveryDTO result = ticketDeliveryMapper.toDto(ticketDelivery);
        ticketDeliverySearchRepository.index(ticketDelivery);
        return result;
    }

    @Override
    public TicketDeliveryDTO update(TicketDeliveryDTO ticketDeliveryDTO) {
        log.debug("Request to update TicketDelivery : {}", ticketDeliveryDTO);
        TicketDelivery ticketDelivery = ticketDeliveryMapper.toEntity(ticketDeliveryDTO);
        ticketDelivery = ticketDeliveryRepository.save(ticketDelivery);
        TicketDeliveryDTO result = ticketDeliveryMapper.toDto(ticketDelivery);
        ticketDeliverySearchRepository.index(ticketDelivery);
        return result;
    }

    @Override
    public Optional<TicketDeliveryDTO> partialUpdate(TicketDeliveryDTO ticketDeliveryDTO) {
        log.debug("Request to partially update TicketDelivery : {}", ticketDeliveryDTO);

        return ticketDeliveryRepository
            .findById(ticketDeliveryDTO.getId())
            .map(existingTicketDelivery -> {
                ticketDeliveryMapper.partialUpdate(existingTicketDelivery, ticketDeliveryDTO);

                return existingTicketDelivery;
            })
            .map(ticketDeliveryRepository::save)
            .map(savedTicketDelivery -> {
                ticketDeliverySearchRepository.save(savedTicketDelivery);

                return savedTicketDelivery;
            })
            .map(ticketDeliveryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDeliveryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TicketDeliveries");
        return ticketDeliveryRepository.findAll(pageable).map(ticketDeliveryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDeliveryDTO> findOne(Long id) {
        log.debug("Request to get TicketDelivery : {}", id);
        return ticketDeliveryRepository.findById(id).map(ticketDeliveryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TicketDelivery : {}", id);
        ticketDeliveryRepository.deleteById(id);
        ticketDeliverySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDeliveryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TicketDeliveries for query {}", query);
        return ticketDeliverySearchRepository.search(query, pageable).map(ticketDeliveryMapper::toDto);
    }
}
