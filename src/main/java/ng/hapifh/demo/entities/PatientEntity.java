package ng.hapifh.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patient")
@Data
public class PatientEntity {

    @Id
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "family_name")
    private String familyName;

    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;
}
