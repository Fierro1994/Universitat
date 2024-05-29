# Проект "Университет"

Этот проект представляет собой веб-приложение для управления студентами и преподавателями.

## Функциональность

- Добавление, обновление и удаление студентов
- Добавление, обновление и удаление преподавателей
- Получение информации о студентах и преподавателях

## Технологии

- Java
- Servlet
- JSP
- JDBC
- Maven

## Установка и запуск
1. Клонируйте репозиторий:
   git clone https://github.com/your-username/university.git

2. Запустите   

## Использование

## Для студентов

- Добавление студента:
POST /addStudent

```json
{
  "email": "example@mail.ru",
  "name": "example"
}
```

- Обновление студента:
PUT /updateStudent

```json
{
  "id" : "1"
  "email": "example@mail.ru",
  "name": "example"
}
```

- Удаление студента:
DELETE /removeStudent?id=

- Получение информации о студенте:
GET /getStudent?id= 

## Для курсов

- Добавление курса:
POST /addCourse

```json
{
  "name": "java"
}
```
  
- Обновление курса:
PUT /updateCourse

```json
{
  "id" : "1"
  "name": "java"
}
```

- Удаление курса:
DELETE /removeCourse?id=

- Получение информации о курсе:
GET /getCourse?id=

## Для преподователей

- Добавление преподавателя:
POST /addTeacher


```json
{
  "email": "example@mail.ru",
  "name": "Example"
}
```

- Обновление преподавателя:
PUT /updateTeacher


```json
{
  "id": "1"
  "email": "example@mail.ru",
  "name": "Example"
}
```

- Удаление преподавателя:
DELETE /removeTeacher?id=

- Получение информации о преподователе:
GET /getTeacher?id=


