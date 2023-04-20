package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import java.net.URI;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;

    private String name;

    @Column(length = 2048)
    private String description;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String image;

    private Timestamp startTime;

    private Timestamp endTime;
    private String location;
    @OneToOne
    private MailConfig mailConfig;

    @ManyToOne(fetch = FetchType.EAGER)
    private Organization organization;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<EventRegistration> attendees =new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<Post> posts =new ArrayList<>();

    public Event(String uuid){
        this.uuid=uuid;
    }

    public void merge(Event event){
        if(event.getName()!=null){
            this.name=event.getName();
        }
        if(event.getDescription()!=null){
            this.description=event.getDescription();
        }
        if(event.getLocation()!=null){
            this.location=event.getLocation();
        }
        if(event.getImage()!=null){
            this.image=event.getImage();
        }
        if(event.getLocation()!=null){
            this.location=event.getLocation();
        }
        if(event.getStartTime()!=null){
            this.startTime=event.getStartTime();
        }
        if(event.getEndTime()!=null){
            this.endTime=event.getEndTime();
        }
        if(event.getMailConfig()!=null){
            this.mailConfig=event.getMailConfig();
        }
    }

}
