package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.AdminNetwork;
import sn.finedev.java.repository.AdminNetworkRepository;
import sn.finedev.java.repository.search.AdminNetworkSearchRepository;
import sn.finedev.java.service.AdminNetworkService;
import sn.finedev.java.service.dto.AdminNetworkDTO;
import sn.finedev.java.service.mapper.AdminNetworkMapper;

/**
 * Service Implementation for managing {@link AdminNetwork}.
 */
@Service
@Transactional
public class AdminNetworkServiceImpl implements AdminNetworkService {

    private final Logger log = LoggerFactory.getLogger(AdminNetworkServiceImpl.class);

    private final AdminNetworkRepository adminNetworkRepository;

    private final AdminNetworkMapper adminNetworkMapper;

    private final AdminNetworkSearchRepository adminNetworkSearchRepository;

    public AdminNetworkServiceImpl(
        AdminNetworkRepository adminNetworkRepository,
        AdminNetworkMapper adminNetworkMapper,
        AdminNetworkSearchRepository adminNetworkSearchRepository
    ) {
        this.adminNetworkRepository = adminNetworkRepository;
        this.adminNetworkMapper = adminNetworkMapper;
        this.adminNetworkSearchRepository = adminNetworkSearchRepository;
    }

    @Override
    public AdminNetworkDTO save(AdminNetworkDTO adminNetworkDTO) {
        log.debug("Request to save AdminNetwork : {}", adminNetworkDTO);
        AdminNetwork adminNetwork = adminNetworkMapper.toEntity(adminNetworkDTO);
        adminNetwork = adminNetworkRepository.save(adminNetwork);
        AdminNetworkDTO result = adminNetworkMapper.toDto(adminNetwork);
        adminNetworkSearchRepository.index(adminNetwork);
        return result;
    }

    @Override
    public AdminNetworkDTO update(AdminNetworkDTO adminNetworkDTO) {
        log.debug("Request to update AdminNetwork : {}", adminNetworkDTO);
        AdminNetwork adminNetwork = adminNetworkMapper.toEntity(adminNetworkDTO);
        adminNetwork = adminNetworkRepository.save(adminNetwork);
        AdminNetworkDTO result = adminNetworkMapper.toDto(adminNetwork);
        adminNetworkSearchRepository.index(adminNetwork);
        return result;
    }

    @Override
    public Optional<AdminNetworkDTO> partialUpdate(AdminNetworkDTO adminNetworkDTO) {
        log.debug("Request to partially update AdminNetwork : {}", adminNetworkDTO);

        return adminNetworkRepository
            .findById(adminNetworkDTO.getId())
            .map(existingAdminNetwork -> {
                adminNetworkMapper.partialUpdate(existingAdminNetwork, adminNetworkDTO);

                return existingAdminNetwork;
            })
            .map(adminNetworkRepository::save)
            .map(savedAdminNetwork -> {
                adminNetworkSearchRepository.save(savedAdminNetwork);

                return savedAdminNetwork;
            })
            .map(adminNetworkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminNetworkDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AdminNetworks");
        return adminNetworkRepository.findAll(pageable).map(adminNetworkMapper::toDto);
    }

    public Page<AdminNetworkDTO> findAllWithEagerRelationships(Pageable pageable) {
        return adminNetworkRepository.findAllWithEagerRelationships(pageable).map(adminNetworkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AdminNetworkDTO> findOne(Long id) {
        log.debug("Request to get AdminNetwork : {}", id);
        return adminNetworkRepository.findOneWithEagerRelationships(id).map(adminNetworkMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AdminNetwork : {}", id);
        adminNetworkRepository.deleteById(id);
        adminNetworkSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminNetworkDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of AdminNetworks for query {}", query);
        return adminNetworkSearchRepository.search(query, pageable).map(adminNetworkMapper::toDto);
    }
}
