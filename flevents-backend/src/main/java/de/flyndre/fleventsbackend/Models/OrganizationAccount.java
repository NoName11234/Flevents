package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
/**
 * This Class is the Modelclass for OrganizationAccounts.
 * It provides getter as well as setter.
 * @implNote This Model is O/R-Mapped to a Database
 * @author Lukas Burkhardt
 * @version $I$
 */
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
