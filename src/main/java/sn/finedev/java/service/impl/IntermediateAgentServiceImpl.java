package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.IntermediateAgent;
import sn.finedev.java.repository.IntermediateAgentRepository;
import sn.finedev.java.repository.search.IntermediateAgentSearchRepository;
import sn.finedev.java.service.IntermediateAgentService;
import sn.finedev.java.service.dto.IntermediateAgentDTO;
import sn.finedev.java.service.mapper.IntermediateAgentMapper;

/**
 * Service Implementation for managing {@link IntermediateAgent}.
 */
@Service
@Transactional
public class IntermediateAgentServiceImpl implements IntermediateAgentService {

    private final Logger log = LoggerFactory.getLogger(IntermediateAgentServiceImpl.class);

    private final IntermediateAgentRepository intermediateAgentRepository;

    private final IntermediateAgentMapper intermediateAgentMapper;

    private final IntermediateAgentSearchRepository intermediateAgentSearchRepository;

    public IntermediateAgentServiceImpl(
        IntermediateAgentRepository intermediateAgentRepository,
        IntermediateAgentMapper intermediateAgentMapper,
        IntermediateAgentSearchRepository intermediateAgentSearchRepository
    ) {
        this.intermediateAgentRepository = intermediateAgentRepository;
        this.intermediateAgentMapper = intermediateAgentMapper;
        this.intermediateAgentSearchRepository = intermediateAgentSearchRepository;
    }

    @Override
    public IntermediateAgentDTO save(IntermediateAgentDTO intermediateAgentDTO) {
        log.debug("Request to save IntermediateAgent : {}", intermediateAgentDTO);
        IntermediateAgent intermediateAgent = intermediateAgentMapper.toEntity(intermediateAgentDTO);
        intermediateAgent = intermediateAgentRepository.save(intermediateAgent);
        IntermediateAgentDTO result = intermediateAgentMapper.toDto(intermediateAgent);
        intermediateAgentSearchRepository.index(intermediateAgent);
        return result;
    }

    @Override
    public IntermediateAgentDTO update(IntermediateAgentDTO intermediateAgentDTO) {
        log.debug("Request to update IntermediateAgent : {}", intermediateAgentDTO);
        IntermediateAgent intermediateAgent = intermediateAgentMapper.toEntity(intermediateAgentDTO);
        intermediateAgent = intermediateAgentRepository.save(intermediateAgent);
        IntermediateAgentDTO result = intermediateAgentMapper.toDto(intermediateAgent);
        intermediateAgentSearchRepository.index(intermediateAgent);
        return result;
    }

    @Override
    public Optional<IntermediateAgentDTO> partialUpdate(IntermediateAgentDTO intermediateAgentDTO) {
        log.debug("Request to partially update IntermediateAgent : {}", intermediateAgentDTO);

        return intermediateAgentRepository
            .findById(intermediateAgentDTO.getId())
            .map(existingIntermediateAgent -> {
                intermediateAgentMapper.partialUpdate(existingIntermediateAgent, intermediateAgentDTO);

                return existingIntermediateAgent;
            })
            .map(intermediateAgentRepository::save)
            .map(savedIntermediateAgent -> {
                intermediateAgentSearchRepository.save(savedIntermediateAgent);

                return savedIntermediateAgent;
            })
            .map(intermediateAgentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IntermediateAgentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all IntermediateAgents");
        return intermediateAgentRepository.findAll(pageable).map(intermediateAgentMapper::toDto);
    }

    public Page<IntermediateAgentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return intermediateAgentRepository.findAllWithEagerRelationships(pageable).map(intermediateAgentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IntermediateAgentDTO> findOne(Long id) {
        log.debug("Request to get IntermediateAgent : {}", id);
        return intermediateAgentRepository.findOneWithEagerRelationships(id).map(intermediateAgentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete IntermediateAgent : {}", id);
        intermediateAgentRepository.deleteById(id);
        intermediateAgentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IntermediateAgentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of IntermediateAgents for query {}", query);
        return intermediateAgentSearchRepository.search(query, pageable).map(intermediateAgentMapper::toDto);
    }
}
