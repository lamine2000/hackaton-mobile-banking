package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.SupplyRequest;
import sn.finedev.java.repository.SupplyRequestRepository;
import sn.finedev.java.repository.search.SupplyRequestSearchRepository;
import sn.finedev.java.service.SupplyRequestService;
import sn.finedev.java.service.dto.SupplyRequestDTO;
import sn.finedev.java.service.mapper.SupplyRequestMapper;

/**
 * Service Implementation for managing {@link SupplyRequest}.
 */
@Service
@Transactional
public class SupplyRequestServiceImpl implements SupplyRequestService {

    private final Logger log = LoggerFactory.getLogger(SupplyRequestServiceImpl.class);

    private final SupplyRequestRepository supplyRequestRepository;

    private final SupplyRequestMapper supplyRequestMapper;

    private final SupplyRequestSearchRepository supplyRequestSearchRepository;

    public SupplyRequestServiceImpl(
        SupplyRequestRepository supplyRequestRepository,
        SupplyRequestMapper supplyRequestMapper,
        SupplyRequestSearchRepository supplyRequestSearchRepository
    ) {
        this.supplyRequestRepository = supplyRequestRepository;
        this.supplyRequestMapper = supplyRequestMapper;
        this.supplyRequestSearchRepository = supplyRequestSearchRepository;
    }

    @Override
    public SupplyRequestDTO save(SupplyRequestDTO supplyRequestDTO) {
        log.debug("Request to save SupplyRequest : {}", supplyRequestDTO);
        SupplyRequest supplyRequest = supplyRequestMapper.toEntity(supplyRequestDTO);
        supplyRequest = supplyRequestRepository.save(supplyRequest);
        SupplyRequestDTO result = supplyRequestMapper.toDto(supplyRequest);
        supplyRequestSearchRepository.index(supplyRequest);
        return result;
    }

    @Override
    public SupplyRequestDTO update(SupplyRequestDTO supplyRequestDTO) {
        log.debug("Request to update SupplyRequest : {}", supplyRequestDTO);
        SupplyRequest supplyRequest = supplyRequestMapper.toEntity(supplyRequestDTO);
        supplyRequest = supplyRequestRepository.save(supplyRequest);
        SupplyRequestDTO result = supplyRequestMapper.toDto(supplyRequest);
        supplyRequestSearchRepository.index(supplyRequest);
        return result;
    }

    @Override
    public Optional<SupplyRequestDTO> partialUpdate(SupplyRequestDTO supplyRequestDTO) {
        log.debug("Request to partially update SupplyRequest : {}", supplyRequestDTO);

        return supplyRequestRepository
            .findById(supplyRequestDTO.getId())
            .map(existingSupplyRequest -> {
                supplyRequestMapper.partialUpdate(existingSupplyRequest, supplyRequestDTO);

                return existingSupplyRequest;
            })
            .map(supplyRequestRepository::save)
            .map(savedSupplyRequest -> {
                supplyRequestSearchRepository.save(savedSupplyRequest);

                return savedSupplyRequest;
            })
            .map(supplyRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplyRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SupplyRequests");
        return supplyRequestRepository.findAll(pageable).map(supplyRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplyRequestDTO> findOne(Long id) {
        log.debug("Request to get SupplyRequest : {}", id);
        return supplyRequestRepository.findById(id).map(supplyRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SupplyRequest : {}", id);
        supplyRequestRepository.deleteById(id);
        supplyRequestSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplyRequestDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SupplyRequests for query {}", query);
        return supplyRequestSearchRepository.search(query, pageable).map(supplyRequestMapper::toDto);
    }
}
