package ng.hapifh.demo.repository;

import ng.hapifh.demo.entities.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {

    List<OrganizationEntity> findByNameIgnoreCase(String name);
}

