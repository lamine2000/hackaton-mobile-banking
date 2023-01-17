package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.Transac;
import sn.finedev.java.repository.TransacRepository;
import sn.finedev.java.repository.search.TransacSearchRepository;
import sn.finedev.java.service.TransacService;
import sn.finedev.java.service.dto.TransacDTO;
import sn.finedev.java.service.mapper.TransacMapper;

/**
 * Service Implementation for managing {@link Transac}.
 */
@Service
@Transactional
public class TransacServiceImpl implements TransacService {

    private final Logger log = LoggerFactory.getLogger(TransacServiceImpl.class);

    private final TransacRepository transacRepository;

    private final TransacMapper transacMapper;

    private final TransacSearchRepository transacSearchRepository;

    public TransacServiceImpl(
        TransacRepository transacRepository,
        TransacMapper transacMapper,
        TransacSearchRepository transacSearchRepository
    ) {
        this.transacRepository = transacRepository;
        this.transacMapper = transacMapper;
        this.transacSearchRepository = transacSearchRepository;
    }

    @Override
    public TransacDTO save(TransacDTO transacDTO) {
        log.debug("Request to save Transac : {}", transacDTO);
        Transac transac = transacMapper.toEntity(transacDTO);
        transac = transacRepository.save(transac);
        TransacDTO result = transacMapper.toDto(transac);
        transacSearchRepository.index(transac);
        return result;
    }

    @Override
    public TransacDTO update(TransacDTO transacDTO) {
        log.debug("Request to update Transac : {}", transacDTO);
        Transac transac = transacMapper.toEntity(transacDTO);
        transac = transacRepository.save(transac);
        TransacDTO result = transacMapper.toDto(transac);
        transacSearchRepository.index(transac);
        return result;
    }

    @Override
    public Optional<TransacDTO> partialUpdate(TransacDTO transacDTO) {
        log.debug("Request to partially update Transac : {}", transacDTO);

        return transacRepository
            .findById(transacDTO.getId())
            .map(existingTransac -> {
                transacMapper.partialUpdate(existingTransac, transacDTO);

                return existingTransac;
            })
            .map(transacRepository::save)
            .map(savedTransac -> {
                transacSearchRepository.save(savedTransac);

                return savedTransac;
            })
            .map(transacMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransacDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transacs");
        return transacRepository.findAll(pageable).map(transacMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransacDTO> findOne(Long id) {
        log.debug("Request to get Transac : {}", id);
        return transacRepository.findById(id).map(transacMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transac : {}", id);
        transacRepository.deleteById(id);
        transacSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransacDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Transacs for query {}", query);
        return transacSearchRepository.search(query, pageable).map(transacMapper::toDto);
    }
}
