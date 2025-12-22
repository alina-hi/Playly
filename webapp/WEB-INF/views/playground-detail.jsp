<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${playground.name} - Playly</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Playly 🎠</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/">На главную</a>
                <c:if test="${not empty sessionScope.user}">
                    <a class="nav-link" href="${pageContext.request.contextPath}/profile">Профиль</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/logout">Выйти</a>
                </c:if>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Главная</a></li>
                <li class="breadcrumb-item active">${playground.name}</li>
            </ol>
        </nav>

        <div class="row">
            <div class="col-md-8">
                <h1>${playground.name}</h1>
                <p class="lead">📍 ${playground.address}</p>

                <c:if test="${not empty playground.description}">
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Описание</h5>
                            <p class="card-text">${playground.description}</p>
                        </div>
                    </div>
                </c:if>

                <div class="row mb-4">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Информация</h5>
                                <ul class="list-unstyled">
                                    <li><strong>Поверхность:</strong> ${playground.surfaceType}</li>
                                    <li><strong>Возраст:</strong> ${playground.ageCategory}</li>
                                    <li><strong>Освещение:</strong> ${playground.illuminated ? 'Есть' : 'Нет'}</li>
                                    <li><strong>Ограждение:</strong> ${playground.fenced ? 'Есть' : 'Нет'}</li>
                                    <li><strong>Тень:</strong> ${playground.hasShade ? 'Есть' : 'Нет'}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Удобства</h5>
                                <div class="mb-2">
                                    <c:if test="${playground.hasParking}">
                                        <span class="badge bg-success me-2">🅿️ Парковка</span>
                                    </c:if>
                                    <c:if test="${playground.hasToilet}">
                                        <span class="badge bg-success me-2">🚻 Туалет</span>
                                    </c:if>
                                    <c:if test="${playground.hasVideoSurveillance}">
                                        <span class="badge bg-success">📹 Видеонаблюдение</span>
                                    </c:if>
                                </div>
                                <c:if test="${playground.avgSafetyRating != null}">
                                    <div class="mt-3">
                                        <h6>Рейтинг безопасности: ${playground.avgSafetyRating}/5</h6>
                                        <div class="progress">
                                            <div class="progress-bar bg-warning"
                                                 style="width: ${playground.avgSafetyRating * 20}%"></div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card mb-4">
                    <div class="card-body">
                        <h5 class="card-title">Аттракционы</h5>
                        <div class="amenities">
                            <c:forEach var="amenity" items="${playground.amenities}">
                                <span class="badge bg-primary me-1 mb-1">${amenity}</span>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card sticky-top" style="top: 20px;">
                    <div class="card-body">
                        <h5 class="card-title">Карта</h5>
                        <div id="miniMap" style="height: 300px; width: 100%;"></div>
                        <div class="mt-3">
                            <button class="btn btn-primary w-100" onclick="openInMaps()">
                                📱 Открыть в навигаторе
                            </button>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty sessionScope.user}">
                    <div class="card mt-3">
                        <div class="card-body">
                            <h5 class="card-title">Ваша оценка</h5>
                            <form id="ratingForm">
                                <div class="mb-3">
                                    <label class="form-label">Безопасность</label>
                                    <select class="form-select" name="safety">
                                        <option value="5">5 - Отлично</option>
                                        <option value="4">4 - Хорошо</option>
                                        <option value="3">3 - Удовлетворительно</option>
                                        <option value="2">2 - Плохо</option>
                                        <option value="1">1 - Опасно</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Чистота</label>
                                    <select class="form-select" name="cleanliness">
                                        <option value="5">5 - Идеально</option>
                                        <option value="4">4 - Чисто</option>
                                        <option value="3">3 - Нормально</option>
                                        <option value="2">2 - Грязновато</option>
                                        <option value="1">1 - Очень грязно</option>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-success w-100">Оценить</button>
                            </form>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Скрипты -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script>
        // Мини-карта
        const miniMap = L.map('miniMap').setView([59.93, 30.31], 14);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(miniMap);

        // Добавляем маркер текущей площадки
        L.marker([59.93, 30.31])
            .addTo(miniMap)
            .bindPopup("${playground.name}")
            .openPopup();

        function openInMaps() {
            const address = encodeURIComponent("${playground.address}");
            // Для Яндекс.Карт
            window.open(`https://yandex.ru/maps/?text=${address}`, '_blank');
        }

        // Обработка формы оценки
        document.getElementById('ratingForm')?.addEventListener('submit', function(e) {
            e.preventDefault();
            alert('Функция оценки скоро будет доступна!');
        });
    </script>
</body>
</html>