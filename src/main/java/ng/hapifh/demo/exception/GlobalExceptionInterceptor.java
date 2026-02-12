package ng.hapifh.demo.exception;


import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.OperationOutcome;

import java.io.IOException;

@Slf4j
@Interceptor
public class GlobalExceptionInterceptor {

    @Hook(value = Pointcut.SERVER_HANDLE_EXCEPTION, order = -1)
    public boolean handleException(RequestDetails requestDetails,
                                   BaseServerResponseException exception,
                                   HttpServletResponse response) throws IOException {

        log.info("XXXXXXXXXXXXXXXXX");
        //
        OperationOutcome oo = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent issue = oo.addIssue();
        issue.setSeverity(OperationOutcome.IssueSeverity.ERROR);
        issue.setCode(OperationOutcome.IssueType.EXCEPTION);

        //
        int status = exception.getStatusCode();
        response.setStatus(status);
        issue.setDiagnostics(exception.getMessage());

        if (status >= 500) {
            issue.setDiagnostics("Service is not available");
        }

        //
        response.setContentType("application/fhir+json;charset=UTF-8");

        // JSON write out the operation outcome
        String json = requestDetails.getServer().getFhirContext()
                .newJsonParser()
                .setPrettyPrint(true)
                .encodeResourceToString(oo);
        log.error("Internal exception: {}", json);

        response.getWriter().write(json);
        response.getWriter().flush();

        // do not continuse the exception handling
        return false;
    }
}

