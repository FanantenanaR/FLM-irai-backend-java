# IRAI Backend - FLM

Backend Java pour le projet IRAI (Information et Registre pour l'Administration Interne) de la FLM (Fiangonana Loterana Malagasy).

## Description

Ce projet est une API REST construite avec Spring Boot qui gere la structure organisationnelle hierarchique de la FLM. Il permet de gerer les differentes entites ecclesiastiques selon leur hierarchie :

```
FOIBE (Siege central)
  └── SYNODA (Synode regional)
        └── FILEOVANA (District)
              └── FITANDREMANA (Zone pastorale)
                    └── FIANGONANA (Paroisse locale)
```

## Technologies utilisees

| Technologie | Version | Description |
|-------------|---------|-------------|
| Java | 17 | Langage de programmation |
| Spring Boot | 4.0.2 | Framework backend |
| Spring Data JPA | - | Persistance des donnees |
| PostgreSQL | - | Base de donnees relationnelle |
| Lombok | - | Reduction du code boilerplate |
| Gradle | - | Gestionnaire de build |

## Prerequis

Avant de commencer, assurez-vous d'avoir installe :

- **Java 17** ou superieur
- **PostgreSQL 12** ou superieur (avec l'extension `ltree` activee)
- **Gradle 8.x** (ou utilisez le wrapper inclus)

### Configuration PostgreSQL

1. Creer la base de donnees :

```sql
CREATE DATABASE irai_db;
```

## Installation

### 1. Cloner le projet

```bash
git clone https://github.com/votre-organisation/FLM-irai-backend-java.git
cd FLM-irai-backend-java
```

### 2. Configurer l'application

Copier le fichier de configuration exemple et le personnaliser :

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Modifier `application.properties` avec vos parametres :

```properties
spring.application.name=irai

# Configuration du serveur
server.servlet.context-path=/irai

# Configuration de la base de donnees PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/irai_db
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Compiler et lancer l'application

```bash
# Avec le wrapper Gradle (recommande)
./gradlew bootRun

# Ou sur Windows
gradlew.bat bootRun
```

L'application demarre sur `http://localhost:8080/irai`

### 4. Verifier l'installation

```bash
curl http://localhost:8080/irai/api/health/ping
```

Reponse attendue :

```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Pong",
  "data": "pong",
  "timestamp": "2026-01-29T10:00:00Z"
}
```

## Structure du projet

```
src/main/java/com/flm/irai/
├── IraiApplication.java           # Point d'entree de l'application
├── common/                        # Module commun (partage)
│   ├── config/
│   │   └── JpaConfig.java         # Configuration JPA et audit
│   ├── controller/
│   │   └── HealthCheckController.java  # Endpoints de sante
│   ├── dto/
│   │   ├── ApiResponse.java       # Format de reponse API standardise
│   │   └── ResponseStatus.java    # Enum des statuts de reponse
│   ├── entity/
│   │   └── AbstractAuditableEntity.java  # Entite de base avec audit
│   └── exception/
│       ├── GlobalExceptionHandler.java   # Gestionnaire global d'exceptions
│       └── ResourceNotFoundException.java # Exception personnalisee
└── entite/                  # Module entite
    ├── controller/
    ├── model/
    ├── repository/
    └── service/
```

## API Endpoints

### Health Check

| Methode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/health` | Statut de l'application |
| GET | `/api/health/ping` | Test de connectivite |

### Organisations

| Methode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/v1/organisations` | Liste paginee des organisations |
| GET | `/api/v1/organisations/{id}` | Recuperer une organisation par ID |
| POST | `/api/v1/organisations` | Creer une nouvelle organisation |
| PUT | `/api/v1/organisations/{id}` | Modifier une organisation |
| DELETE | `/api/v1/organisations/{id}` | Supprimer une organisation (soft delete) |


## Format des reponses API

Toutes les reponses suivent un format standardise :

### Reponse de succes

```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Message descriptif",
  "data": { ... },
  "timestamp": "2026-01-29T10:00:00Z"
}
```

### Reponse de succes paginee

```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Liste recuperee avec succes",
  "data": [ ... ],
  "page": 0,
  "limit": 20,
  "total_element": 150,
  "number_page": 8,
  "timestamp": "2026-01-29T10:00:00Z"
}
```

### Reponse d'erreur

```json
{
  "status": "ERROR",
  "code": 404,
  "message": "Ressource non trouvee",
  "error": "NOT_FOUND",
  "error_message": "Organisation avec l'id xxx non trouve",
  "timestamp": "2026-01-29T10:00:00Z"
}
```

## Modele de donnees

### Colonnes d'audit (presentes sur toutes les entites principales)

| Colonne | Type | Description |
|---------|------|-------------|
| id | UUID | Identifiant unique |
| date_creation | Timestamp | Date de creation (auto) |
| cree_par | UUID | ID de l'utilisateur createur |
| date_modification | Timestamp | Date de derniere modification (auto) |
| modifie_par | UUID | ID du dernier modificateur |
| date_suppression | Timestamp | Date de suppression (soft delete) |

### Soft Delete

L'application utilise le soft delete : les donnees ne sont jamais physiquement supprimees. Quand une entite est "supprimee", le champ `date_suppression` est renseigne avec la date courante. Les requetes standard ne retournent que les entites non supprimees grace a l'annotation `@SQLRestriction("date_suppression IS NULL")`.

## Dependances principales

```groovy
dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Base de donnees
    runtimeOnly 'org.postgresql:postgresql'
    
    // Dev tools
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    
    // Tests
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

## Conventions de developpement

### Structure des packages

- `common/` : Code partage entre les modules (config, dto, exceptions, entites de base)
- `{module}/controller/` : Controllers REST
- `{module}/service/` : Logique metier
- `{module}/repository/` : Acces aux donnees (Spring Data JPA)
- `{module}/model/` : Entites JPA
- `{module}/dto/` : dto (Data Transfer Object)


### Conventions de nommage

- **Classes** : PascalCase (ex: `OrganisationService`)
- **Methodes/Variables** : camelCase (ex: `findByCode`)
- **Colonnes BD** : snake_case (ex: `date_creation`)
- **Endpoints API** : kebab-case et pluriel (ex: `/api/v1/organisations`)


## Tests

Lancer les tests :

```bash
./gradlew test
```

## Auteurs

Projet FLM - Equipe de developpement IRAI

## Licence

Ce projet est la propriete de la FLM (Fiangonana Loterana Malagasy).
