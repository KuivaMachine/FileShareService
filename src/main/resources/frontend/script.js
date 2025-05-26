document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');
    const upload_button = document.getElementById('upload_button');
    const delete_button = document.getElementById('delete-btn');
    const logoutBtn = document.getElementById('logout-btn');
    const download_container = document.getElementById('download-container');
    const fileInput = document.getElementById('file-input');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const errorElement = document.getElementById('error-message');
    let user_id = null;


    // Проверка авторизации при загрузке страницы
    checkAuth();

    delete_button.addEventListener('commit', function () {
      linkElement.style.display = 'none'
      upload_button.style.display = 'none'
      showMessage('Выберите файл для загрузки');
     });


    fileInput.addEventListener('change', function () {
        if (this.files.length > 0) {
            upload_button.style.display = 'flex'
        }
    });
    // Обработка формы входа
    loginForm.addEventListener('submit', async function (e) {
        e.preventDefault(); //предотвращает перезагрузку страницы

        let user = username.value.trim();
        let pass = password.value.trim();

        // Валидация
        if (!pass || !user) {
            errorElement.textContent = 'Заполните поля';
            errorElement.style.display = 'block';
            return;
        }

        if (user.length < 4) {
            errorElement.textContent = 'Короткий логин!\nМинимум 4 символа';
            errorElement.style.display = 'block';
            return;
        }

        if (pass.length < 5) {
            errorElement.textContent = 'Короткий пароль!\nМинимум 6 символов';
            errorElement.style.display = 'block';
            return;
        }
        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username: user,
                    password: pass  })
            });
            if (response.ok) {
                const data = await response.json();
                user_id = data.userId;
                const date = new Date();
                date.setDate(date.getDate() + 5);
                document.cookie = `user_id=${user_id};expires=${date.toUTCString()}; path=/`;
                showAuthorizedUI();
                loadStatistic();
            } else {
                errorElement.textContent = 'Ошибка аутенфикации';
                errorElement.style.display = 'block';
            }
        } catch (error) {
            console.error('Ошибка:', error);
            errorElement.textContent = 'Ошибка сети';
            errorElement.style.display = 'block';
        }
    });

    //Обработка формы загрузки файла
    upload_button.addEventListener('click', async function (e) {
        e.preventDefault();
        const file = fileInput.files[0];
        if (file.size > 1073741824) { //примерно 1 Гб
            showMessage("Размер файла не может превышать 1Гб");
            return
        }

        const input = document.querySelector('.input');
         if (input) input.checked = true;
        showMessage('Загрузка файла...', 'info');

        try {
            const response = await fetch('/upload', {
                method: 'POST',
                headers: {
                    'X-File-Name': encodeURIComponent(file.name),
                    'User-Id': encodeURIComponent(user_id),
                    'X-File-Size': file.size
                },
                body: file
            });

            if (response.ok) {
                const data = await response.json();
                               completeUploadAnimation(() => {
                               showMessage('Файл успешно загружен', 'success');
                               showDownloadLink(data.downloadUrl);
                               loadStatistic();
                               download_container.style.display = 'inline-flex'
                               upload_button.style.display = 'none';
                });

            }
        } catch (error) {
            showMessage(error);
        }
    });
 // Функция для завершающей анимации
    function completeUploadAnimation(callback) {
        setTimeout(() => {

            if (callback) callback();
        }, 4000);
    }

    // Выход из системы
    logoutBtn.addEventListener('click', () => {
        user_id = null;
        document.cookie = 'user_id=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
            showUnauthorizedUI();
    });

    // Проверка авторизации
    async function checkAuth() {
        const cookies = document.cookie.split(';').map(c => c.trim());
        const userIdCookie = cookies.find(c => c.startsWith('user_id='));

        if (userIdCookie) {
            user_id = userIdCookie.substring('user_id='.length)
            showAuthorizedUI();
            loadStatistic();
        }
    }


    username.addEventListener('click', () => {
        errorElement.style.display = 'none';
    })
    password.addEventListener('click', () => {
        errorElement.style.display = 'none';
    })

    //Загрузка статистики
    async function loadStatistic() {
        try {
            const response = await fetch('/statistic', {
                headers: {
                    'user_id': user_id
                }
            });

            if (response.ok) {
                const stats = await response.json();
                showFiles(stats);
            }
        } catch (error) {
            console.error('Ошибка загрузки статистики:', error);
        }
    }

    //Отображение статистики
    function showFiles(stats) {
        const tbody = document.querySelector('#stats-table tbody');
        tbody.innerHTML = '';

        stats.forEach(file => {
            const row = document.createElement('tr');

            const nameCell = document.createElement('td');
            nameCell.textContent = file.file_name || 'Неизвестный файл';


            const sizeCell = document.createElement('td');
            sizeCell.textContent = formatFileSize(file.size);

            const dateCell = document.createElement('td');
            let last_downloaded = new Date(file.last_downloaded).toLocaleString()
            if (last_downloaded === '01.01.1970, 03:00:00') {
                dateCell.textContent = 'Не загружался'
            } else {
                dateCell.textContent = last_downloaded
            }

            const linkCell = document.createElement('td');
            const link = document.createElement('a');
            link.href = `/download/${file.id}`;
            link.textContent = 'Скачать';
            linkCell.appendChild(link);

            const deleteCell = document.createElement('td');
            const deleteBtn = document.createElement('button');
            deleteBtn.classList.add('delete');
            deleteBtn.textContent = 'Удалить';
            deleteBtn.addEventListener('click', async () => {
                try {
                    const response = await fetch(`/delete_file/${file.id}`, {
                        method: 'GET'
                    });

                    if (response.status === 204) {
                        row.remove(); //Удаляем строку таблицы

                    } else {
                        throw new Error('Ошибка удаления');
                    }
                } catch (error) {
                    showMessage(error.message, 'error');
                }
            });
            deleteCell.appendChild(deleteBtn);

            row.appendChild(nameCell);
            row.appendChild(sizeCell);
            row.appendChild(dateCell);
            row.appendChild(linkCell);
            row.appendChild(deleteCell);

            tbody.appendChild(row);
        });
    }

    //Форматирование размера файла
    function formatFileSize(bytes) {

        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'Kb', 'Mb', 'Gb'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    //Показать интерфейс для авторизованных пользователей
    function showAuthorizedUI() {
        document.getElementById('auth-section').style.display = 'none';
        document.getElementById('upload-section').style.display = 'block';
        document.getElementById('stats-section').style.display = 'block';
        document.getElementById('logout-btn').style.display = 'block';
        logoutBtn.style.display = 'block';
    }

    //Показать интерфейс для неавторизованных пользователей
    function showUnauthorizedUI() {
        document.getElementById('auth-section').style.display = 'block';
        document.getElementById('upload-section').style.display = 'none';
        document.getElementById('stats-section').style.display = 'none';
        logoutBtn.style.display = 'none';
    }

    //Показать сообщение о статусе
    function showMessage(message) {
        const statusElement = document.getElementById('upload-status');
        statusElement.textContent = message;
    }

    //Показать ссылку для скачивания
    function showDownloadLink(url) {
        const linkElement = document.getElementById('download-link');
         linkElement.style.display = 'block'
        linkElement.href = url;
        linkElement.textContent = window.location.origin + url;

    }
});