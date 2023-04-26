package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
/**
 * This Class is the Modelclass for the FleventsAccount.
 * It provides getter as well as setter and a Merge-Method.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FleventsAccount {
    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    private String firstname;
    private String lastname;
    private Boolean isActive;
    private Boolean isPlatformAdmin = false;
    @Column(unique = true)
    private String email;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String icon;

    private String secret;

    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<EventRegistration> events = new ArrayList<>();
    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<OrganizationAccount> organisations = new ArrayList<>();

    public FleventsAccount(String uuid){
        this.uuid=uuid;
    }

    public void merge(FleventsAccount account){
        if(account.getFirstname()!=null){
            this.firstname=account.getFirstname();
        }
        if(account.getLastname()!=null){
            this.lastname=account.getLastname();
        }
        if(account.getEmail()!=null){
            this.email=account.getEmail();
        }
        if(account.getIcon()!=null){
            this.icon=account.getIcon();
        }
        if(account.secret!=null){
            this.secret=account.getSecret();
        }
        if(account.getIsActive()!=null){
            this.isActive=account.getIsActive();
        }
        if(account.getIsPlatformAdmin()!=null){
            this.isPlatformAdmin=account.getIsPlatformAdmin();
        }
    }



}
