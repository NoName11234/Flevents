package de.flyndre.fleventsbackend.Models;
/**
 * This Class is the Enumeration for the Roles in an Event.
 * @author Lukas Burkhardt
 * @version $I$
 */
public enum EventRole implements Role{
    invited,
    guest,
    attendee,
    tutor,
    organizer
}
