package ng.hapifh.demo.mapper;

import lombok.RequiredArgsConstructor;
import ng.hapifh.demo.entities.OrganizationEntity;
import ng.hapifh.demo.entities.PatientEntity;
import ng.hapifh.demo.exception.FhirResourceNotFoundException;
import ng.hapifh.demo.repository.OrganizationRepository;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PatientMapper {

    private final OrganizationRepository organizationRepository;

    public PatientEntity toEntity(Patient patient) {
        PatientEntity entity = new PatientEntity();

        entity.setId(UUID.randomUUID());

        // name
        HumanName name = patient.getNameFirstRep();
        entity.setGivenName(name.getGivenAsSingleString());
        entity.setFamilyName(name.getFamily());

        // gender
        if (patient.hasGender()) {
            entity.setGender(patient.getGender().toCode());
        }

        // birthdate
        if (patient.hasBirthDate()) {
            entity.setBirthDate(
                    patient.getBirthDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
            );
        }

        // phone
        patient.getTelecom().stream()
                .filter(t -> t.getSystem() == ContactPoint.ContactPointSystem.PHONE)
                .findFirst()
                .ifPresent(t -> entity.setPhone(t.getValue()));

        // managing organization
        if (patient.hasManagingOrganization()) {
            String orgId = patient.getManagingOrganization().getReferenceElement().getIdPart();
            OrganizationEntity org = organizationRepository
                    .findById(UUID.fromString(orgId))
                    .orElseThrow(() ->
                            new FhirResourceNotFoundException("Organization not found: " + orgId));
            entity.setOrganization(org);
        }

        return entity;
    }

    public Patient toFhir(PatientEntity entity) {
        Patient patient = new Patient();

        patient.setId(new IdType("Patient", entity.getId().toString()));

        patient.addName()
                .setFamily(entity.getFamilyName())
                .addGiven(entity.getGivenName());

        if (entity.getGender() != null) {
            patient.setGender(
                    Enumerations.AdministrativeGender.fromCode(entity.getGender())
            );
        }

        if (entity.getBirthDate() != null) {
            patient.setBirthDate(
                    Date.from(
                            entity.getBirthDate()
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                    )
            );
        }

        if (entity.getPhone() != null) {
            patient.addTelecom()
                    .setSystem(ContactPoint.ContactPointSystem.PHONE)
                    .setValue(entity.getPhone());
        }

        if (entity.getOrganization() != null) {
            patient.setManagingOrganization(
                    new Reference("Organization/" + entity.getOrganization().getId())
            );
        }

        return patient;
    }


}
