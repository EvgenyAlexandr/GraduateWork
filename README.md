# Ads — Backend платформы по перепродаже вещей

Backend-приложение для платформы по перепродаже вещей. Дипломный проект курса Java-разработчик SkyPro.

API реализовано по [OpenAPI-спецификации](openapi.yaml) и совместимо с [готовым фронтендом](https://github.com/skypro-backend/example-for-graduate-work).

## Функционал

- авторизация и аутентификация пользователей;
- роли **USER** и **ADMIN**;
- CRUD для объявлений и комментариев;
- загрузка и отображение изображений объявлений и аватаров.

### Текущий статус — Этап IV ✅

- аутентификация через PostgreSQL (`CustomUserDetailsService` + BCrypt);
- контроллеры подключены к сервисам и репозиториям;
- проверка прав: USER — только свои объявления/комментарии, ADMIN — любые;
- обработка 401/403/404 через `GlobalExceptionHandler`;
- **изображения**: сохранение на диск, URL в БД, публичная раздача по `/images/**`.

## Работа с изображениями (Этап IV)

Файлы хранятся на диске (каталог задаётся в `application.properties`), в PostgreSQL сохраняется только URL.

| Действие | Эндпоинт | Описание |
|----------|----------|----------|
| Создать объявление с фото | `POST /ads` (multipart) | Поле `image` + `properties` (JSON) |
| Обновить фото объявления | `PATCH /ads/{id}/image` | Ответ: байты изображения |
| Обновить аватар | `PATCH /users/me/image` | Multipart, поле `image` |
| Получить байты файла | `GET /images/ads/{файл}` | **Без авторизации** |
| Получить аватар | `GET /images/avatars/{файл}` | **Без авторизации** |
| URL в JSON | поля `image`, `authorImage` | Путь с корня, напр. `/images/ads/uuid_photo.jpg` |

Фронтенд собирает полный адрес: `http://localhost:8080` + значение из поля `image`.

**Конфигурация** (`application.properties`):

```properties
app.storage.base-path=images
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

Каталог `images/` добавлен в `.gitignore` — загруженные файлы не попадают в git.

## Стек технологий

| Категория | Технология |
|-----------|------------|
| Язык | Java 11 |
| Сборка | Maven |
| Фреймворк | Spring Boot 2.7 |
| ORM | Spring Data JPA / Hibernate |
| БД | PostgreSQL + Liquibase |
| Маппинг | MapStruct |
| Безопасность | Spring Security (Basic Auth) |
| Документация API | SpringDoc OpenAPI |
| Утилиты | Lombok |

## Быстрый старт

### PostgreSQL

Приложение подключается к PostgreSQL по умолчанию:

| Параметр | Значение по умолчанию |
|----------|----------------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/ads` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |

**Вариант 1 — Docker (рекомендуется):**

```bash
docker compose up -d
```

Контейнер создаст базу `ads` автоматически. Затем запустите приложение из IntelliJ IDEA или через Maven.

**Вариант 2 — локальный PostgreSQL:**

Сначала создайте базу (один раз):

```sql
CREATE DATABASE ads;
```

Через `psql`:

```bash
psql -U postgres -c "CREATE DATABASE ads;"
```

**IntelliJ IDEA:** Run → Edit Configurations → Environment variables (если логин/пароль другие):

```
DB_URL=jdbc:postgresql://localhost:5432/ads;DB_USER=postgres;DB_PASSWORD=postgres
```

### Запуск бэкенда

```bash
.\mvnw.cmd spring-boot:run
```

При старте Liquibase автоматически применит миграции из `src/main/resources/db/changelog/`.

### Запуск фронтенда

```bash
docker run -p 3000:3000 --rm ghcr.io/dmitry-bizin/front-react-avito:v1.21
```

### Сборка и тесты

```bash
.\mvnw.cmd clean test
```

Тесты используют профиль `test` с H2 in-memory (Liquibase отключён).

| Тип | Пакет | Назначение |
|-----|-------|------------|
| Unit | `mapper/`, `security/`, `service/`, `util/` | Мапперы, AccessChecker, ImageStorageService |
| Integration | `controller/`, `service/` | MockMvc + H2: Security, CRUD, загрузка изображений |

## Документация API

| URL | Описание |
|-----|----------|
| http://localhost:8080/swagger-ui.html | Swagger UI |
| http://localhost:8080/v3/api-docs | OpenAPI JSON |

## Структура проекта

```
src/main/java/ru/skypro/homework/
├── entity/          # JPA-сущности (User, Ad, Comment)
├── repository/      # Spring Data JPA репозитории
├── mapper/          # MapStruct-мапперы Entity ↔ DTO
├── service/         # Интерфейсы сервисов + ImageStorageService
│   └── impl/        # AdServiceImpl, UserServiceImpl, CommentServiceImpl, AuthServiceImpl
├── config/          # WebSecurityConfig, WebConfig (раздача /images/**)
├── security/        # UserDetailsService, AccessChecker
├── exception/       # GlobalExceptionHandler
├── util/            # ImageUrlUtils (кодирование URL для браузера)
├── dto/             # DTO по OpenAPI
└── controller/      # AdsController, CommentsController, UserController, AuthController

src/main/resources/db/changelog/   # Liquibase-миграции
images/                            # Загруженные файлы (не в git)
```

## Модель данных

```
users (id, email, password, first_name, last_name, phone, role, image)
  ↑                    ↑
  │                    └── comment.author_id
  └── ad.author_id

ad (pk, title, description, price, image, author_id)
  ↑
  └── comment.ad_pk
```

Поле `image` в `users` и `ad` хранит URL вида `/images/avatars/...` или `/images/ads/...`.

## Этапы разработки

| Этап | Содержание | Статус |
|------|------------|--------|
| I | DTO, контроллеры | ✅ |
| II | Сущности, репозитории, мапперы, БД | ✅ |
| III | Auth, сервисы, контроллеры + БД | ✅ |
| IV | Работа с картинками | ✅ |


## Исходный шаблон

https://github.com/skypro-backend/example-for-graduate-work
