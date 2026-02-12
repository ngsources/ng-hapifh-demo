package ng.hapifh.demo.provider;


import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import ng.hapifh.demo.service.OrganizationService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrganizationProvider implements IResourceProvider {

    private final OrganizationService organizationService;

    @Override
    public Class<Organization> getResourceType() {
        return Organization.class;
    }


    @Read
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public Organization read(@IdParam IdType id) {
        return organizationService.getOrganization(id.getIdPart());
    }


    @Create
    @PreAuthorize("hasRole('WRITE')")
    public MethodOutcome create(@ResourceParam Organization organization) {
        return organizationService.createOrganization(organization);
    }


    @Search
    @PreAuthorize("hasAnyRole('READ', 'WRITE')")
    public List<Organization> search(@RequiredParam(name = Organization.SP_NAME) StringParam name) {
        if (name != null && name.getValue() != null && name.getValue().length() < 3) {
            throw new InvalidRequestException("Search parameter 'name' must be at least 3 characters long");
        }
        return organizationService.searchByName(name);
    }
}

