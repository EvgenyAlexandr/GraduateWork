# Ads — Backend платформы по перепродаже вещей

Backend-приложение для платформы по перепродаже вещей. Дипломный проект курса Java-разработчик SkyPro.

API реализовано по [OpenAPI-спецификации](openapi.yaml) и совместимо с [готовым фронтендом](https://github.com/skypro-backend/example-for-graduate-work).

## Функционал

- авторизация и аутентификация пользователей;
- роли **USER** и **ADMIN**;
- CRUD для объявлений и комментариев;
- загрузка и отображение изображений объявлений и аватаров.

### Текущий статус — Этап I

Реализованы DTO и контроллеры-скелеты: все эндпоинты из спецификации доступны и возвращают значения по умолчанию (пустые объекты DTO).

## Стек технологий

| Категория | Технология |
|-----------|------------|
| Язык | Java 11 |
| Сборка | Maven |
| Фреймворк | Spring Boot 2.7 |
| ORM | Spring Data JPA / Hibernate |
| Безопасность | Spring Security (Basic Auth) |
| БД | H2 (dev), PostgreSQL (prod) |
| Документация API | SpringDoc OpenAPI (Swagger UI) |
| Утилиты | Lombok |

## Быстрый старт

### Требования

- JDK 11+
- Maven (или встроенный `mvnw`)
- Docker Desktop — для запуска фронтенда

### Запуск фронтенда

```bash
docker run -p 3000:3000 --rm ghcr.io/dmitry-bizin/front-react-avito:v1.21
```

Фронтенд: http://localhost:3000

### Запуск бэкенда

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Бэкенд: http://localhost:8080

### Сборка и тесты

```bash
.\mvnw.cmd clean test
```

## Документация API

| URL | Описание |
|-----|----------|
| http://localhost:8080/swagger-ui.html | Swagger UI |
| http://localhost:8080/v3/api-docs | OpenAPI JSON |

## Тестовый пользователь (Basic Auth)

| Поле | Значение |
|------|----------|
| Логин | `user@gmail.com` |
| Пароль | `password` |

Примеры запросов — в файле [example-requests.http](example-requests.http).

## Структура проекта

```
src/main/java/ru/skypro/homework/
├── HomeworkApplication.java
├── config/WebSecurityConfig.java
├── constant/ApiConstants.java
├── controller/
│   ├── AdsController.java
│   ├── AuthController.java
│   └── UserController.java
├── dto/
└── util/DefaultDtoFactory.java
```

## Этапы разработки

| Этап | Содержание | Статус |
|------|------------|--------|
| I | DTO, контроллеры | ✅ |
| II | Сущности, репозитории, мапперы | ⏳ |
| III | Сервисы | ⏳ |
| IV | Работа с картинками, демо | ⏳ |

## Исходный шаблон

https://github.com/skypro-backend/example-for-graduate-work
