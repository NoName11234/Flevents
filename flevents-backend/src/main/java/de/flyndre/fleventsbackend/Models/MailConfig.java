package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
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
public class MailConfig {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    private String registerMessage;
    private String infoMessage;
    private LocalDateTime infoMessageTime;
    private String feedbackMessage;
    private LocalDateTime feedbackMessageTime;

    public void merge(MailConfig mailConfig){
        if(mailConfig.registerMessage!=null){
            this.registerMessage=mailConfig.getRegisterMessage();
        }
        if(mailConfig.infoMessage !=null){
            this.infoMessage =mailConfig.getInfoMessage();
        }
        if(mailConfig.infoMessageTime !=null){
            this.infoMessageTime =mailConfig.getInfoMessageTime();
        }
        if(mailConfig.feedbackMessageTime !=null){
            this.feedbackMessage =mailConfig.getFeedbackMessage();
        }
        if(mailConfig.feedbackMessageTime !=null){
            this.feedbackMessageTime =mailConfig.getFeedbackMessageTime();
        }
    }
}
