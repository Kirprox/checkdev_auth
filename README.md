## checkdev_auth

### Описание проекта

`checkdev_auth` — это микросервис авторизации и управления пользователями, построенный на базе **Spring Boot**, **Spring Security** и **Spring Data JPA**.
Он обеспечивает аутентификацию, регистрацию, хранение профилей пользователей, работу с фотографиями и управление ролями.
Сервис может использоваться как часть микросервисной экосистемы CheckDev и интегрироваться с другими модулями через REST API.


### Основные возможности

* **Регистрация и авторизация** пользователей.
* Поддержка **OAuth2** (через `OAuthCall` и `UserDetailsDefinition`).
* **Управление ролями и правами** доступа (Role, RoleService, SecurityConfig).
* **Работа с профилями** (Profile, ProfileService, ProfileController).
* **Хранение и загрузка фотографий** (Photo, DownloadController).
* **CORS-фильтрация** для разрешения междоменных запросов.
* **Liquibase** для управления миграциями БД.


### Архитектура

```
ru.checkdev.auth/
├── AuthSrv.java                 # Точка входа (Spring Boot)
├── config/                      # Конфигурации безопасности
│   └── SecurityConfig.java
├── domain/                      # JPA-сущности
│   ├── Photo.java
│   ├── Profile.java
│   └── Role.java
├── dto/                         # Data Transfer Objects
│   ├── ProfileDTO.java
│   └── ProfileTgDTO.java
├── filter/                      # Кастомные фильтры
│   └── CorsFilter.java
├── repository/                  # Spring Data JPA репозитории
│   ├── PersonRepository.java
│   ├── PhotoRepository.java
│   └── RoleRepository.java
├── service/                     # Бизнес-логика
│   ├── PersonService.java
│   ├── PhotoService.java
│   ├── ProfileService.java
│   └── RoleService.java
├── util/                        # Вспомогательные классы
│   ├── OAuthCall.java
│   ├── PhotoMainClass.java
│   └── UserDetailsDefinition.java
└── web/controller/              # REST-контроллеры
    ├── AuthController.java
    ├── DownloadController.java
    ├── PersonController.java
    └── ProfileController.java
```


### Используемые технологии

| Компонент                       | Назначение                     |
|---------------------------------| ------------------------------ |
| **Java 17+**                    | Язык реализации                |
| **Spring Boot**                 | Фреймворк приложения           |
| **Spring Security**             | Аутентификация и авторизация   |
| **Spring Data JPA (Hibernate)** | Работа с базой данных          |
| **Liquibase**                   | Управление миграциями БД       |
| **Maven**                       | Система сборки                 |
| **Jenkins**                     | CI/CD (согласно `Jenkinsfile`) |
| **REST API**                    | Общение с внешними сервисами   |
| **Logback**                     | Логирование                    |


### Конфигурация приложения

`src/main/resources/application.properties` (пример):

```properties
spring.application.name=auth
server.port=9010

spring.datasource.url=jdbc:postgresql://localhost:5432/checkdev_auth
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.liquibase.change-log=classpath:db/db.changelog-master.xml

spring.security.user.name=admin
spring.security.user.password=admin

eureka.client.service-url.defaultZone=http://localhost:9009/eureka
```


### База данных

* **ORM:** Hibernate (через Spring Data JPA)
* **Миграции:** Liquibase (`src/main/resources/db/db.changelog-master.xml`)
* **Основные таблицы:**

    * `profile` — данные пользователей
    * `role` — роли
    * `photo` — изображения профиля


### API (основные эндпоинты)

| Метод                 | Путь                            | Назначение |
| --------------------- | ------------------------------- | ---------- |
| `POST /auth/login`    | Авторизация пользователя        |            |
| `POST /auth/register` | Регистрация нового пользователя |            |
| `GET /profile/{id}`   | Получение профиля               |            |
| `PUT /profile/{id}`   | Обновление профиля              |            |
| `GET /photo/{id}`     | Получение фотографии            |            |
| `POST /photo/upload`  | Загрузка новой фотографии       |            |

*(точные пути могут отличаться, в зависимости от аннотаций в контроллерах)*


### Как запустить проект локально

#### 1. Сборка проекта

```bash
mvn clean package
```

#### 2. Запуск приложения

```bash
java -jar target/checkdev_auth-0.0.1-SNAPSHOT.jar
```

или:

```bash
mvn spring-boot:run
```

#### 3. Доступ

После запуска сервис будет доступен по адресу:
[http://localhost:9010](http://localhost:9010)


### Jenkins (CI/CD)

Файл `Jenkinsfile` используется для автоматической сборки и деплоя.
Обычно включает стадии:

1. **Build** — `mvn clean package`
2. **Test** — запуск юнит-тестов
3. **Deploy** — публикация артефакта (например, в Docker Registry или на сервер)


### Развёртывание в Docker (пример)

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/checkdev_auth-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9010
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Сборка:

```bash
docker build -t checkdev-auth .
```

Запуск:

```bash
docker run -p 9010:9010 checkdev-auth
```


### Интеграция с Eureka

Для регистрации в Eureka Server (например, `cd_eureka` на порту 9009):

```properties
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:9009/eureka
```