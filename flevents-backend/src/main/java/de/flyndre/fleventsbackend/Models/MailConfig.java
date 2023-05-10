package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * This Class is the Config for Mailings.
 * It provides  a Merge-Method.
 * @implNote This Model is O/R-Mapped to a Database
 * @author Lukas Burkhardt
 * @version $I$
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailConfig implements Cloneable {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    private String registerMessage="";
    private String infoMessage="";
    private Duration infoMessageOffset = Duration.ofHours(1);
    private String feedbackMessage ="";
    private Duration feedbackMessageOffset=Duration.ofHours(1);
    private String organizationInvitation= "";
    private String eventInvitation="";



    public void merge(MailConfig mailConfig){
        if(mailConfig.registerMessage!=null){
            this.registerMessage=mailConfig.getRegisterMessage();
        }
        if(mailConfig.infoMessage !=null){
            this.infoMessage =mailConfig.getInfoMessage();
        }
        if(mailConfig.infoMessageOffset !=null){
            this.infoMessageOffset =mailConfig.getInfoMessageOffset();
        }
        if(mailConfig.feedbackMessage !=null){
            this.feedbackMessage =mailConfig.getFeedbackMessage();
        }
        if(mailConfig.feedbackMessageOffset !=null){
            this.feedbackMessageOffset =mailConfig.getFeedbackMessageOffset();
        }
        if(mailConfig.organizationInvitation !=null){
            this.organizationInvitation =mailConfig.getOrganizationInvitation();
        }
        if(mailConfig.eventInvitation !=null){
            this.eventInvitation =mailConfig.getEventInvitation();
        }

    }

    /**
     * Copy the MailConfig by value
     * @return a new {@link MailConfig} object with the same values but with a null uuid
     */
    public MailConfig copy() {
        return new MailConfig(null,registerMessage,infoMessage,infoMessageOffset,feedbackMessage,feedbackMessageOffset,organizationInvitation,eventInvitation);
    }
}
