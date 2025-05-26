<div align="center">
  <h1>📥 File Share Service</h1>
</div>
Cервис, который позволяет загружать файлы и получать временную ссылку на скачивание.



📌 Features

    Загрузка файлов, получение ссылки на скачивание
    Форма загрузки файла
    Статистика по загруженным файлам
    Загрузка файлов только для авторизованных пользователей
    Стек: Vanilla JS, CSS, HTML
    

⚙️ Требования

    Java 21+
    Docker
    Gradle (опционально, можно использовать gradlew)

<div align="left">
  <h2>🛠️ Инструкция по запуску</h2>
</div>

Скачайте репозиторий:

    git clone https://github.com/KuivaMachine/FileShareService.git

Перейдите в директорию с программой:
    
    cd FileShareService

Запустите PostgreSQL в Docker:

    docker-compose up -d postgres

Соберите и запустите приложение:

    ./gradlew build 
    java -jar  build/libs/FileShareService-1.0.jar

Сервис запущен!

    Доступен по адресу: http://localhost:9092/


Остановка сервиса:

    Нажмите Ctrl+C в терминале, где работает приложение.

<div align="center">
 <picture>
  <img src="https://github.com/KuivaMachine/FileShareService/blob/master/src/main/resources/image.png" alt="Описание изображения">
</picture>
</div>
