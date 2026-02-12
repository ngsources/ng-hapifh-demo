package ng.hapifh.demo.provider;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ng.hapifh.demo.service.PatientService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Component
public class PatientProvider implements IResourceProvider {

    private final PatientService service;

    public PatientProvider(PatientService service) {
        this.service = service;
    }

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    @Read
    public Patient read(@IdParam IdType id) {
        return service.getPatient(id.getIdPart());
    }

    @PreAuthorize("hasRole('WRITE')")
    @Create
    public MethodOutcome create(@ResourceParam Patient patient) {
        return service.createPatient(patient);
    }

    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    @Search
    public List<Patient> search(
            @RequiredParam(name = Patient.SP_NAME)
            StringParam name) {

        if (name != null && name.getValue() != null && name.getValue().length() < 3) {
            throw new InvalidRequestException("Search parameter 'name' must be at least 3 characters long");
        }

        List<Patient> patients = service.searchByName(name);
        return patients;
    }

}

