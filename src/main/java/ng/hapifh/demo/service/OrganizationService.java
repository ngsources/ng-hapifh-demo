package ng.hapifh.demo.service;


import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.hapifh.demo.entities.OrganizationEntity;
import ng.hapifh.demo.exception.FhirResourceNotFoundException;
import ng.hapifh.demo.repository.OrganizationRepository;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public Organization getOrganization(String id) {
        OrganizationEntity entity = organizationRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new FhirResourceNotFoundException("Organization not found: " + id));

        Organization org = new Organization();
        org.setId(entity.getId().toString());
        org.setName(entity.getName());
        return org;
    }


    public MethodOutcome createOrganization(Organization organization) {

        OrganizationEntity entity = new OrganizationEntity();
        entity.setName(organization.getName());

        organizationRepository.save(entity);

        Organization org = new Organization();
        org.setId(new IdType("Organization", entity.getId().toString()));
        org.setName(entity.getName());



        MethodOutcome outcome = new MethodOutcome();
        outcome.setId(new IdType("Organization", entity.getId().toString()));
        outcome.setCreated(true);
        outcome.setResource(org);

        log.trace("Organization is created");
        return outcome;
    }


    public List<Organization> searchByName(StringParam name) {
        List<OrganizationEntity> entities = organizationRepository.findByNameIgnoreCase(name.getValue());
        log.trace("Organization entriues from db:{}", entities);
        return entities.stream()
                .map(entity -> {
                    Organization org = new Organization();
                    org.setId(new IdType("Organization", entity.getId().toString()));
                    org.setName(entity.getName());
                    return org;
                })
                .toList();
    }
}

