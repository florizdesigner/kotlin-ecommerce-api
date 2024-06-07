## API of endpoint /users

* В запросах на создание и изменение пользователя необходимо передавать header = "Content-Type: application/json". Тело запроса передается в формате JSON.
* Формат ответов (используемые data classes):
  * **POST /users**, **GET /users/id**, **PUT /users/id** | ApiSuccessResponse (успех), ApiResponse (ошибка)
  * **GET /users** | UsersResponse(успех), ApiResponse (неуспех)
  * **DELETE /users/id** | ApiResponse(успех / неуспех)

### Создание пользователя (POST /users)
* Обязательные параметры: first_name (имя), last_name (фамилия), email (электронная почта пользователя). Опциональных параметров нет.
* Ограничения по параметрам: 
  * first_name (максимум 50 символов включительно; только английские символы); 
  * last_name (от 1 до 50 символов включительно; только английские символы);
  * email (regexp для формата e-mail; только английские символы; обязательно уникальное значение в БД);
* Если параметр не проходит валидацию => вернуть ошибку.
* При передаче уже существующего e-mail - возвращаем ошибку.
* Пример запроса: <code>curl --location '{{host_url}}/users' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "first_name": "Test",
  "last_name": "User",
  "email": "testuser@test2.com"
  }'</code>

### Получение информации о пользователе по ID (GET /users/{id})
* ID - обязательный параметр запроса, в формате Int. При корректном ID и наличии юзера в БД = возвращается ответ с информацией о нем (см. Формат ответов).
* Если в БД нет информации по указанному ID = вернуть ошибку.
* Если параметр передан некорректно = вернуть ошибку.
* Пример запроса: <code>curl --location --request GET '{{host_url}}/users/9'</code>

### Получение всех пользователей (GET /users)
* Пример запроса: <code>curl --location --request GET '{{host_url}}/users'</code>

### Изменение данных пользователя (PUT /users/{id})
* Обязательные параметры: ID пользователя; новые данные пользователя (CreateUserRequest) в формате JSON.
* Валидация полей тела запроса аналогична созданию заказа.
* Должен проверяться e-mail при изменении данных (если юзер с такой почтой уже есть - отдавать ошибку).
* Пример запроса: <code>curl --location --request PUT '{{host_url}}/users/9' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "first_name": "Tester",
  "last_name": "Userovich",
  "email": "testuser@test22.com"
  }'</code>

### Удаление пользователя (DELETE /users/{id})
* ID - обязательный параметр запроса (формат Int). При передаче корректного ID и наличии записи в БД, происходит ее удаление.
* Если ID в неверном формате или отсутствует в БД - вернуть ошибку.
* Пример запроса: <code>curl --location --request DELETE '{{host_url}}/users/9'</code>