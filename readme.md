# 5D Hub Microservices

## Структура проекта

- **eureka_server** 
- **d_hub_user_service**
- **d_hub_company_service**
- **gateway_routes**

## Стек технологий

- Java 17
- Maven
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Liquibase
- Swagger
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Docker


## Основные REST запросы для удобства тестирования в Postman:

**Пользователи (Users):**

*   **POST http://localhost:8083/users**

    ```json
    [
      {
        "firstName": "Alice",
        "lastName": "Smith",
        "phoneNumber": "123-456-7890",
        "height": 165,
        "companyId": 1
      },
      {
        "firstName": "Bob",
        "lastName": "Johnson",
        "phoneNumber": "987-654-3210",
        "height": 180,
        "companyId": 1
      },
      {
        "firstName": "Charlie",
        "lastName": "Williams",
        "phoneNumber": "555-123-4567",
        "height": 172,
        "companyId": 2
      },
      {
        "firstName": "Diana",
        "lastName": "Brown",
        "phoneNumber": "111-222-3333",
        "height": 158,
        "companyId": 2
     }
    ]
    ```

    **Запросы на получения данных от Users без парсинга Company:**
*   **GET http://localhost:8083/users/by-surname?last_name=Smith**

*   **GET http://localhost:8083/users/by-id?user_id=1**
    
    **Запросы на получения данных от Users извлеченных из Company:**

*   **GET http://localhost:8083/users_companies**

*   **GET http://localhost:8083/users_companies/by-surname?surname=Smith**

*   **GET http://localhost:8083/users_companies/1**
    
---

**Компании (Companies):**

*   **POST http://localhost:8083/companies (Tech Solutions Inc.)****

    ```json
    {
      "name": "Tech Solutions Inc.",
      "budget": 5000000.00,
      "employeeIds": [1, 2]
    }
    
    ```

*   **POST http://localhost:8083/companies (Global Innovations Ltd.)**

    ```json
    {
      "name": "Global Innovations Ltd.",
      "budget": 7500000.00,
      "employeeIds": [3, 4]
    }
    ```
**Получение данных для компании без парсинга пользователя:**

*   **GET http://localhost:8083/companies/by-id?company_id=1**

*   **GET http://localhost:8083/companies/all**

**Получение данных для компании извлеченных из пользователя:**

*   **Получение всех сотрудников компании по названию компании**

    *   Для GET-запроса для компании "Tech Solutions Inc.":
        **GET http://localhost:8083/proxy/companies/Tech%20Solutions%20Inc./employees**

*   **Получение всех компаний и их сотрудников**

    *   **GET http://localhost:8083/proxy/companies/all**


### **Сборка и запуск**

Проект включает файл `docker-compose.yml`, который создает контейнеры для всех микросервисов и базы данных.

Соберите и запустите проект с помощью Docker Compose:
docker-compose up --build


### **Примечания**

Данные микросервисы предусмотрены для управления данными с сотрудниками и компаниями,
умеют работать со своими данными, а также могут извлекать данные друг у друга.
Они регистрируются в Eureka и умеют работать через единый порт Gateway (8083).
Микросервисы и база данных поднимаются через Docker Compose.