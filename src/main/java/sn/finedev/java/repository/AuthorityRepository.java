package sn.finedev.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.finedev.java.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
