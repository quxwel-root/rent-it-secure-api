# Rent-It Secure API 🦾

### 📌 Project Overview
Цей проект є захищеним бекендом для системи оренди обладнання. Він автоматизує процес реєстрації, логіну, перевірки наявності товару та безпечного створення замовлень.

### 🚀 Key Technical Features:
* **Spring Security + JWT**: Повна авторизація без сесій (stateless).
* **BCrypt Hashing**: Паролі зберігаються у вигляді незворотних хешів.
* **JPA & Hibernate**: Оптимізовані зв'язки `@ManyToOne` з використанням Lazy Loading.
* **Transactional Logic**: Гарантія цілісності даних при створенні замовлень.

### 🛠 Tech Stack:
- Java 17+
- Spring Boot 3
- Spring Security
- JSON Web Token (jjwt)
- Maven
- H2 / PostgreSQL

### 📖 API Endpoints:
- `POST /api/auth/register` - Реєстрація нового юзера.
- `POST /api/auth/login` - Отримання JWT токена.
- `GET /api/devices/available` - Перегляд вільних девайсів.
- `POST /api/orders` - Створення замовлення (вимагає Bearer Token).