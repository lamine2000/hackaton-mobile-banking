package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.Town;
import sn.finedev.java.repository.TownRepository;
import sn.finedev.java.repository.search.TownSearchRepository;
import sn.finedev.java.service.TownService;
import sn.finedev.java.service.dto.TownDTO;
import sn.finedev.java.service.mapper.TownMapper;

/**
 * Service Implementation for managing {@link Town}.
 */
@Service
@Transactional
public class TownServiceImpl implements TownService {

    private final Logger log = LoggerFactory.getLogger(TownServiceImpl.class);

    private final TownRepository townRepository;

    private final TownMapper townMapper;

    private final TownSearchRepository townSearchRepository;

    public TownServiceImpl(TownRepository townRepository, TownMapper townMapper, TownSearchRepository townSearchRepository) {
        this.townRepository = townRepository;
        this.townMapper = townMapper;
        this.townSearchRepository = townSearchRepository;
    }

    @Override
    public TownDTO save(TownDTO townDTO) {
        log.debug("Request to save Town : {}", townDTO);
        Town town = townMapper.toEntity(townDTO);
        town = townRepository.save(town);
        TownDTO result = townMapper.toDto(town);
        townSearchRepository.index(town);
        return result;
    }

    @Override
    public TownDTO update(TownDTO townDTO) {
        log.debug("Request to update Town : {}", townDTO);
        Town town = townMapper.toEntity(townDTO);
        town = townRepository.save(town);
        TownDTO result = townMapper.toDto(town);
        townSearchRepository.index(town);
        return result;
    }

    @Override
    public Optional<TownDTO> partialUpdate(TownDTO townDTO) {
        log.debug("Request to partially update Town : {}", townDTO);

        return townRepository
            .findById(townDTO.getId())
            .map(existingTown -> {
                townMapper.partialUpdate(existingTown, townDTO);

                return existingTown;
            })
            .map(townRepository::save)
            .map(savedTown -> {
                townSearchRepository.save(savedTown);

                return savedTown;
            })
            .map(townMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TownDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Towns");
        return townRepository.findAll(pageable).map(townMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TownDTO> findOne(Long id) {
        log.debug("Request to get Town : {}", id);
        return townRepository.findById(id).map(townMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Town : {}", id);
        townRepository.deleteById(id);
        townSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TownDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Towns for query {}", query);
        return townSearchRepository.search(query, pageable).map(townMapper::toDto);
    }
}
