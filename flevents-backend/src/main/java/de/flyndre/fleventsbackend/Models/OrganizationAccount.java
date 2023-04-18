package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationAccount {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;

    @ManyToOne(fetch = FetchType.EAGER)
    private Organization organization;
    @ManyToOne(fetch = FetchType.EAGER)
    private FleventsAccount account;


    private OrganizationRole role;

}
