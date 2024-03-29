package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
/**
 * This Class is the Modelclass for Organizations.
 * It provides getter as well as setter and a Merge-Method.
 * @implNote This Model is O/R-Mapped to a Database
 * @author Lukas Burkhardt
 * @version $I$
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    @NotBlank
    private String name;
    private String description;
    private String address;
    private String phoneContact;
    @Column(unique = true)
    private String customerNumber;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String icon;

    @OneToOne
    private MailConfig mailConfig=new MailConfig();

    @OneToMany(mappedBy ="organization",fetch = FetchType.LAZY)
    private List<OrganizationAccount> accounts = new ArrayList<>();
    @OneToMany(mappedBy = "organization",fetch = FetchType.EAGER)
    private List<Event> events = new ArrayList<>();

    public void merge(Organization organization){
        if(organization.getName()!=null){
            this.name=organization.getName();
        }
        if(organization.getDescription()!=null){
            this.description=organization.getDescription();
        }
        if(organization.getIcon()!=null){
            this.icon=organization.getIcon();
        }
        if(organization.getAddress()!=null){
            this.address=organization.getAddress();
        }
        if(organization.getPhoneContact()!=null){
            this.phoneContact=organization.getPhoneContact();
        }
        if(organization.getCustomerNumber()!=null){
            this.customerNumber=organization.getCustomerNumber();
        }
    }
}
