package de.flyndre.fleventsbackend;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.dtos.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableAutoConfiguration
public class AppConfiguration {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        Converter<List<OrganizationAccount>, List<AccountPreview>> convertAccountToAccountPreview = new AbstractConverter<>() {
            protected List<AccountPreview> convert(List<OrganizationAccount> organisationAccounts) {
                return organisationAccounts.stream().map(organisationAccount -> {
                    AccountPreview accountPreview = modelMapper.map(organisationAccount.getAccount(),AccountPreview.class);
                    accountPreview.setRole(organisationAccount.getRole());
                    return accountPreview;
                }).collect(Collectors.toList());
            }
        };
        modelMapper.typeMap(Organization.class, OrganizationInformation.class).addMappings(mapper -> mapper.using(convertAccountToAccountPreview).map(Organization::getAccounts,OrganizationInformation::setAccountPreviews));

        Converter<List<OrganizationAccount>, List<OrganizationPreview>> convertOrganizationToOrganizationPreview = new AbstractConverter<>() {
            protected List<OrganizationPreview> convert(List<OrganizationAccount> organisationAccounts) {
                return organisationAccounts.stream().map(organisationAccount -> {
                    OrganizationPreview organizationPreview = modelMapper.map(organisationAccount.getOrganization(),OrganizationPreview.class);
                    organizationPreview.setRole(organisationAccount.getRole());
                    return organizationPreview;
                }).collect(Collectors.toList());
            }
        };
        modelMapper.typeMap(FleventsAccount.class, AccountInformation.class).addMappings(mapper -> mapper.using(convertOrganizationToOrganizationPreview).map(FleventsAccount::getOrganisations,AccountInformation::setOrganizationPreviews));

        modelMapper.typeMap(Event.class, EventInformation.class).addMapping(Event::getOrganization,EventInformation::setOrganizationPreview);

        Converter<List<Event>,List<EventPreview>> convertEventToEventPreview = new AbstractConverter<>() {

            protected List<EventPreview> convert(List<Event> events) {
                return events.stream().map(event -> modelMapper.map(event,EventPreview.class)).collect(Collectors.toList());
            }
        };
        modelMapper.typeMap(Organization.class,OrganizationInformation.class).addMappings(mapper -> mapper.using(convertEventToEventPreview).map(Organization::getEvents,OrganizationInformation::setEventPreviews));

        Converter<List<EventRegistration>,List<EventPreview>> convertEventRegistrationToEventPreview = new AbstractConverter<>() {
            protected List<EventPreview> convert(List<EventRegistration> eventRegistrations) {
                return eventRegistrations.stream().map(eventRegistration -> {
                    EventPreview eventPreview = modelMapper.map(eventRegistration.getEvent(),EventPreview.class);
                    eventPreview.setRole(eventRegistration.getRole());
                    return eventPreview;
                }).collect(Collectors.toList());
            }
        };
        modelMapper.typeMap(FleventsAccount.class,AccountInformation.class).addMappings(mapper -> mapper.using(convertEventRegistrationToEventPreview).map(FleventsAccount::getEvents,AccountInformation::setEventPreviews));

        Converter<List<EventRegistration>,List<AccountPreview>> convertEventRegistrationToAccountPreview = new AbstractConverter<>() {
            protected List<AccountPreview> convert(List<EventRegistration> eventRegistrations) {
                return eventRegistrations.stream().map(eventRegistration -> {
                    AccountPreview accountPreview = modelMapper.map(eventRegistration.getAccount(),AccountPreview.class);
                    accountPreview.setRole(eventRegistration.getRole());
                    return accountPreview;
                }).collect(Collectors.toList());
            }
        };
        modelMapper.typeMap(Event.class, EventInformation.class).addMappings(mapper ->mapper.using(convertEventRegistrationToAccountPreview).map(Event::getAttendees,EventInformation::setAccountPreviews));

        //modelMapper.typeMap(Post.class, PostInformation.class).addMapping(Post::getUuid,PostInformation::set)

        return modelMapper;
    }
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


}

