```
 ________ ___  ___       _____ ______   ________  ________  ________  _________  _______      
|\  _____\\  \|\  \     |\   _ \  _   \|\   __  \|\   __  \|\   __  \|\___   ___\\  ___ \     
\ \  \__/\ \  \ \  \    \ \  \\\__\ \  \ \  \|\  \ \  \|\  \ \  \|\  \|___ \  \_\ \   __/|    
 \ \   __\\ \  \ \  \    \ \  \\|__| \  \ \  \\\  \ \   _  _\ \   __  \   \ \  \ \ \  \_|/__  
  \ \  \_| \ \  \ \  \____\ \  \    \ \  \ \  \\\  \ \  \\  \\ \  \ \  \   \ \  \ \ \  \_|\ \ 
   \ \__\   \ \__\ \_______\ \__\    \ \__\ \_______\ \__\\ _\\ \__\ \__\   \ \__\ \ \_______\
    \|__|    \|__|\|_______|\|__|     \|__|\|_______|\|__|\|__|\|__|\|__|    \|__|  \|_______|
```

## Схема базы данных

![This is an image](/schema.png)

# Примеры запросов

## USERS

### 1. Получение списка всех пользователей

#### GET /users

```sql
    SELECT *
    FROM users;
```

### 2. Найти user по его {id}

#### GET /users/{id}

```sql
    SELECT *
    FROM users
    WHERE id = {id};
```

### 3. Найти список друзей для user по его {id}

#### GET /users/{id}/friends

```sql
    SELECT *
    FROM users
    WHERE id IN (SELECT friend_id FROM friends WHERE user_id = {id}); 
```

### 4. Получение списка общих друзей для user по их {id} и {otherId}

#### GET /users/{id}/friends/common/{otherId}

```sql
    SELECT *
    FROM users
    WHERE id IN (SELECT friend_id FROM friends WHERE user_id = {id});
    INTERSECT
    SELECT *
    FROM users
    WHERE id IN (SELECT friend_id FROM friends WHERE user_id = {otherId});
```

### 5. Создание user

#### POST /users

```sql
    INSERT INTO users (email, login, name, birthdate)
    VALUES ('test@email.ru', 'test_user', 'test user_name', '2000-10-13')) RETURNING id;
```

### 6. Обновление user

#### PUT /users

```sql
    UPDATE users
    SET email     = 'test_update@email.ru',
        login     = 'test_user',
        name      = 'test user_name',
        birthdate = TO_DATE('01022019', 'MMDDYYYY')
    WHERE id = 'id';
```

### 7. Добавление друга по его id {friendId} для user по его {id}

#### PUT /users/{id}/friends/{friendId}

```sql
    INSERT INTO friends (user_id, friend_id)
    VALUES ({id}, {friendId});
```

### 8. Удаление друга по его id {friendId} у user по его {id}

#### DELETE /users/{id}/friends/{friendId}

```sql
    DELETE
    FROM friends
    WHERE (user_id = {id} AND friend_id = {friendId};
```

### 8. Удаление user по его {id}

#### DELETE /users/{id}

```sql
    DELETE
    FROM users
    WHERE id = {id} CASCADE;
```

***  

## FILMS

### 1. Получение списка всех film

#### GET /films

```sql
    SELECT * FROM films; 
```

### 2. Найти film по его {id}

#### GET /films/{id}

```sql
    SELECT *
    FROM films AS f
    WHERE f.id = {id};
```

### 3. Создание film

#### POST /films

```sql
    INSERT INTO films (description, name, releaseDate, duration, mpaa_rate_id)
    VALUES ('test_description', 'test_name', '2010-07-15', 120, 2) RETURNING id;
```

### 4. Обновление film

#### PUT /films

```sql
    UPDATE films
    SET description  = 'new_description',
        name         = 'new_test_name',
        releaseDate  = '2010-07-15',
        duration     = 120,
        mpaa_rate_id = 2
    WHERE id = 'id';
```

### 5. Добавление like для film

#### PUT /films/{id}/like/{userId}

```sql
    INSERT INTO likes (film_id, user_id)
    VALUES ({id}, {userId});
```

### 6. Удаление film по его {id}

#### DELETE /films/{id}

```sql
    DELETE
    FROM films
    WHERE id = {id} CASCADE;
```

### 5. Удаление like у film по id film и user

#### DELETE /films/{id}/like/{userId}

```sql
    DELETE
    FROM likes
    WHERE film_id = {id} AND user_id = {userId};
```
### 6. Добавление genre к film

```sql
    INSERT INTO film_genre (film_id, genre_id)
    VALUES (5, 2);
```
***
