package ng.hapifh.demo.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import ng.hapifh.demo.exception.GlobalExceptionInterceptor;
import ng.hapifh.demo.provider.OrganizationProvider;
import ng.hapifh.demo.provider.PatientProvider;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class FhirConfig {

    @Bean
    public RestfulServer restfulServer(GlobalExceptionInterceptor globalExceptionInterceptor) {
        return new RestfulServer(FhirContext.forR4()) {

            @Override
            protected void initialize() throws ServletException {
                registerInterceptor(globalExceptionInterceptor);
            }
        };
    }

    @Bean
    public ServletRegistrationBean<RestfulServer> fhirServlet(
            FhirContext fhirContext,
            RestfulServer server,
            PatientProvider patientProvider,
            OrganizationProvider organizationProvider
    ) {

        server.setResourceProviders(patientProvider, organizationProvider);

        ServletRegistrationBean<RestfulServer> servlet = new ServletRegistrationBean<>(server, "/fhir/*");
        servlet.setName("FHIRServlet");

        return servlet;
    }

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public GlobalExceptionInterceptor globalExceptionInterceptor() {
        return new GlobalExceptionInterceptor();
    }

}
