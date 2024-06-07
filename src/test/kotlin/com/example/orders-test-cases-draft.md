Классы эквивалентности:
* description: 0, [1, 128], [128, +беск]
* amount: [??? -2147483647, 0], [1, 350000], [350001, 2147483647 ????]

description:
* "" = FAIL
* "1" = OK
* "Test phrase for order" = OK
* "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy te" = OK (128)
* "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy tex" = FAIL (129)
* "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book." = FAIL (> 128)

amount:
* 0 = FAIL
* 1 = OK
* 15000 = OK
* 350000 = OK
* 350001 = FAIL
* 1000000 = FAIL
* -1 = FAIL ???
* -10000 = FAIL

userId:
* 5 = OK (есть в БД)
* 15 = FAIL (нет в БД)
* "test" = FAIL (некорректный формат данных)

POST /orders:

Тест-кейсы:
Позитивные:
* "Test phrase for order", 15000, 5 = OK
* "1", 1, 5 = OK
* "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy te", 350000, 5 = OK
* Успешный формат ответа соответствует ApiSuccessOrderResponse, код ответа соответствует документации (201, created)

Негативные:
* "Test phrase for order", 15000, 150 = FAIL (userId not found)
* "", 15000, 5 = FAIL (description is empty)
* "128 < description", 0, 15, = FAIL
* "129 < description", -1000, "test" = FAIL
* Неуспешный формат ответа соответствует ApiResponse, код ответа 400 / 500.
_____________

GET /orders/id
Позитивные:
* id = взять существующий в БД id в верном формате uuid = OK
* Создать заказ, получить ID, сделать запрос GET /users/id = проверить, что инфо совпадает

Негативные:
* id = в верном формате uuid, но несуществующий в БД = FAIL (order not found)
* id в неверном формате (символов != 36 + неподходящий формат uuid) = FAIL
_____________

GET /orders
* Проверить, что ответ отдается согласно спецификации
* Создать заказ, проверить, что он попадает в этот список и информация совпадает с той, что была при создании

_____________

DELETE /orders/id
Позитивные:
* id = взять существующий в БД id в верном формате uuid = OK, ответ на запрос соответствует спецификации (status=success); далее GET /orders/id = order not found (чтобы точно проверить, что заказа нет в БД или сделать запрос в БД руками без использования функции)

Негативные:
* id = в верном формате, но нет в БД = FAIL (order not found)
* id в другом формате (не uuid) = FAIL (ошибка формата id)