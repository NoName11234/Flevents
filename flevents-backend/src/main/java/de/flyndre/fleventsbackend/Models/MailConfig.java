package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    private String alertMessage;
    private LocalDateTime alertMessageTimestamp;
    private String thankMessage;
    private LocalDateTime thankMessageTimestamp;

    public void merge(MailConfig mailConfig){
        if(mailConfig.registerMessage!=null){
            this.registerMessage=mailConfig.getRegisterMessage();
        }
        if(mailConfig.alertMessage!=null){
            this.alertMessage=mailConfig.getAlertMessage();
        }
        if(mailConfig.alertMessageTimestamp!=null){
            this.alertMessageTimestamp=mailConfig.getAlertMessageTimestamp();
        }
        if(mailConfig.thankMessageTimestamp!=null){
            this.thankMessage=mailConfig.getThankMessage();
        }
        if(mailConfig.thankMessageTimestamp!=null){
            this.thankMessageTimestamp=mailConfig.getThankMessageTimestamp();
        }
    }
}
