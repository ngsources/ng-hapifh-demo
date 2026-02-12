package ng.hapifh.demo.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.hapifh.demo.entities.PatientEntity;
import ng.hapifh.demo.exception.FhirResourceNotFoundException;
import ng.hapifh.demo.mapper.PatientMapper;
import ng.hapifh.demo.repository.PatientRepository;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PatientService {

    private final PatientMapper fhirMapper;
    private final PatientRepository patientRepository;


    public Patient getPatient(String id) {
        log.debug("Getting entity, id: %s", id);
        PatientEntity entity = patientRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new FhirResourceNotFoundException("Patient not found: " + id));

        return fhirMapper.toFhir(entity);
    }

    public MethodOutcome createPatient(Patient patient) {
        PatientEntity entity = fhirMapper.toEntity(patient);

        patientRepository.save(entity);

        MethodOutcome outcome = new MethodOutcome();
        outcome.setId(new IdType("Patient", entity.getId().toString()));
        outcome.setCreated(true);
        outcome.setResource(fhirMapper.toFhir(entity));

        log.trace("Patient is created: {}, entity: {}", outcome, entity);
        return outcome;
    }

    public List<Patient> searchByName(StringParam name) {
        List<PatientEntity> entities = patientRepository.findByFamilyNameIgnoreCase(name.getValue());
        log.trace("Search entites: {}", entities);
        return entities.stream().map(entity -> {
            Patient patient = fhirMapper.toFhir(entity);
            return patient;
        }).toList();
    }
}
