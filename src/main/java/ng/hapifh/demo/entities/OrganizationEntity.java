package ng.hapifh.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Table(name = "organization")
@Entity
public class OrganizationEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;
}

