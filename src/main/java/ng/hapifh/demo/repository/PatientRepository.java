package ng.hapifh.demo.repository;

import ng.hapifh.demo.entities.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientRepository
        extends JpaRepository<PatientEntity, UUID> {

    List<PatientEntity> findByFamilyNameIgnoreCase(String familyName);
}

