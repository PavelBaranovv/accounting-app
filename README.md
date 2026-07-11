# Accounting App

Веб-приложение для учета сотрудников, отделов и проектов компании. Система помогает вести внутренние справочники, контролировать проектную прибыль и выгружать отчеты в Excel.

## Возможности

- регистрация и вход пользователей
- разграничение прав доступа для ролей `ADMIN` и `USER`
- управление отделами
- управление сотрудниками и их привязкой к отделам
- управление проектами с расчетом фактической и прогнозной прибыли
- просмотр сводной информации по проектам
- экспорт отчетов по сотрудникам и проектам в формате `xlsx`

## Технологический стек

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Apache POI
- Maven
- Docker Compose

## Скриншоты
<img width="800" alt="image" src="https://github.com/user-attachments/assets/04076c73-6599-47fa-8d9e-ec6acb9a932d" />
<img width="800" alt="image" src="https://github.com/user-attachments/assets/dab40a08-5afe-43a5-9e9c-12abef81535a" />
<img width="800" alt="image" src="https://github.com/user-attachments/assets/8eb091db-f69d-4064-86cf-97f2eebcfd16" />


## Быстрый запуск
```
docker compose up --build
```
