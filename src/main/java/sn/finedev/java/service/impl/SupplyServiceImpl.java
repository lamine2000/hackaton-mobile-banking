package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.Supply;
import sn.finedev.java.repository.SupplyRepository;
import sn.finedev.java.repository.search.SupplySearchRepository;
import sn.finedev.java.service.SupplyService;
import sn.finedev.java.service.dto.SupplyDTO;
import sn.finedev.java.service.mapper.SupplyMapper;

/**
 * Service Implementation for managing {@link Supply}.
 */
@Service
@Transactional
public class SupplyServiceImpl implements SupplyService {

    private final Logger log = LoggerFactory.getLogger(SupplyServiceImpl.class);

    private final SupplyRepository supplyRepository;

    private final SupplyMapper supplyMapper;

    private final SupplySearchRepository supplySearchRepository;

    public SupplyServiceImpl(SupplyRepository supplyRepository, SupplyMapper supplyMapper, SupplySearchRepository supplySearchRepository) {
        this.supplyRepository = supplyRepository;
        this.supplyMapper = supplyMapper;
        this.supplySearchRepository = supplySearchRepository;
    }

    @Override
    public SupplyDTO save(SupplyDTO supplyDTO) {
        log.debug("Request to save Supply : {}", supplyDTO);
        Supply supply = supplyMapper.toEntity(supplyDTO);
        supply = supplyRepository.save(supply);
        SupplyDTO result = supplyMapper.toDto(supply);
        supplySearchRepository.index(supply);
        return result;
    }

    @Override
    public SupplyDTO update(SupplyDTO supplyDTO) {
        log.debug("Request to update Supply : {}", supplyDTO);
        Supply supply = supplyMapper.toEntity(supplyDTO);
        supply = supplyRepository.save(supply);
        SupplyDTO result = supplyMapper.toDto(supply);
        supplySearchRepository.index(supply);
        return result;
    }

    @Override
    public Optional<SupplyDTO> partialUpdate(SupplyDTO supplyDTO) {
        log.debug("Request to partially update Supply : {}", supplyDTO);

        return supplyRepository
            .findById(supplyDTO.getId())
            .map(existingSupply -> {
                supplyMapper.partialUpdate(existingSupply, supplyDTO);

                return existingSupply;
            })
            .map(supplyRepository::save)
            .map(savedSupply -> {
                supplySearchRepository.save(savedSupply);

                return savedSupply;
            })
            .map(supplyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Supplies");
        return supplyRepository.findAll(pageable).map(supplyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplyDTO> findOne(Long id) {
        log.debug("Request to get Supply : {}", id);
        return supplyRepository.findById(id).map(supplyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Supply : {}", id);
        supplyRepository.deleteById(id);
        supplySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplyDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Supplies for query {}", query);
        return supplySearchRepository.search(query, pageable).map(supplyMapper::toDto);
    }
}
