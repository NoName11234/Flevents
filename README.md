# Flevents
Flevents is a Event-Managment Software that is beeing developted as a student project at DHBW Stuttgart Campus Horb.

## The Developers

The Developers of this Project consists of five Students at the DHBW Stuttgart Campus Horb.

| Role | Name |
| --- | --- |
| Project-Leader | Lukas&nbsp;Burkhardt |
| Technical Assistent | Paul&nbsp;Lehmann |
| Reserach- and Testcommissioner | Pascal&nbsp;Fuchs |
| Modelling- und Implementationcommissioner | Ruben&nbsp;Kraft |
| Quality Assurance- und Documentationcomissioner | David&nbsp;Maier |

## Techstack

### Backend

The Backend is beeing developted in Spring Boot. It also uses features of Spring MVC, Spring Data as well as Spring Security.
It consists of a REST-API, with witch the Frontend can communicate.
The Endpoints are saved with JWT-Tokens, wich need to be obtained by registering an Account and perform an Login with it.

### Frontend

The Frontend ist written in Vue.js. TypeScript is beeing used as Scripting-Language in the Frontend.

### Databases

As Object-Relational Mapper Hibernate is beeing used, this enables many Databases wich can be configured in the applications.properties.

## Structure of the software

the Software consists off of two main folders.

### /flevents-backend

Contains the Backend-Application.

```txt
└───flevents-backend/
    │   pom.xml         (required Maven-dependencies)
    │   ...
    ├───test/           (tests-folder)
    └───src/
        ├───resources/  (configuration-files)
        │
        └───main/java/de/flyndre/fleventsbackend/
            │   AppConfiguration.java               (configuration for mappings etc.)
            │   FleventsBackendApplication.java     (the entry-point for the server-application)
            │   WebConfiguration.java
            ├───controller                          (controllers for ahndling incoming requests)
            ├───controllerServices                  (services for controller-logic)
            ├───dtos                                (data-to-object models)
            ├───Models                              (model-classes for data structure)
            ├───repositories                        (definition of repositories for data base access)
            ├───security                            (configuration and logic for handling authentication and authorization)
            └───services                            (services for business logic)
```

### /flevents-frontend

Contains the Frontend-Application.

```txt
flevents-frontend/
│   index.html          (the entry-point for the web-application)
│   package.json        (required npm-dependencies)
│   ...
└───src/
    │   App.vue
    │   constants.ts    (file containing constants for configuration)
    │   main.ts         (main file for frontend-app)
    │   ...
    ├───api/            (endpoint-definitions for the REST-API)
    ├───assets/         (static assets)
    ├───components/     (components/building-blocks für UI)
    ├───layouts/        (layouts for components)
    ├───models/         (models for data for API-communication)
    ├───plugins/        (plugin-definitions)
    ├───router/         (routes of the veiws)
    ├───service/        (services used at various places in the application)
    ├───store/          (frontend-stores state-management)
    ├───styles/         (CSS)
    └───views/          (views for routes)
        ├───accounts/
        ├───error/
        ├───event/
        ├───forms/
        ├───home/
        └───organizations/
```

## Configuration

All Ports, Database-Connections and further Configurations can be adjusted in the Backendfile applications.properties
IMPORTANT: Before you can use this Software you have to set up the applications.properties.
