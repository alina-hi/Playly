<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Playly - Карта площадок</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
        }
        #map {
            height: 600px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .sidebar {
            height: 600px;
            overflow-y: auto;
            background: white;
            border-radius: 8px;
            padding: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .playground-card {
            cursor: pointer;
            transition: all 0.2s;
            margin-bottom: 10px;
            border: 1px solid #dee2e6;
            border-left: 4px solid #4CAF50;
        }
        .playground-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .filter-section {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .navbar-custom {
            background-color: #4CAF50 !important;
        }
        .nav-link-custom {
            color: white !important;
            font-weight: 500;
        }
        .nav-link-custom:hover {
            opacity: 0.8;
        }
    </style>
</head>
<body>
    <!-- Исправленная навигация -->
    <nav class="navbar navbar-expand-lg navbar-custom">
        <div class="container-fluid">
            <a class="navbar-brand nav-link-custom" href="${pageContext.request.contextPath}/profile.html"
               style="font-weight: bold; font-size: 1.3rem;">
                 Playly
            </a>

            <div class="navbar-nav ms-auto">
                <a class="nav-link nav-link-custom" href="${pageContext.request.contextPath}/profile.html">
                     Профиль
                </a>
                <a class="nav-link nav-link-custom" href="#" onclick="return confirmLogout()">
                     Выйти
                </a>
            </div>
        </div>
    </nav>

    <!-- Кнопка назад -->
    <div class="container-fluid mt-3 mb-3">
        <a href="${pageContext.request.contextPath}/profile.html"
           class="btn btn-sm btn-outline-secondary">
            ← Назад в профиль
        </a>
        <h4 class="d-inline-block ms-3">🗺️ Карта детских площадок</h4>
    </div>

    <div class="container-fluid">
        <div class="row">
            <!-- Сайдбар с фильтрами -->
            <div class="col-md-3">
                <div class="filter-section">
                    <h5>🔍 Фильтры поиска</h5>

                    <form method="get" action="${pageContext.request.contextPath}/map">
                        <div class="mb-3">
                            <label class="form-label"><strong>Аттракционы:</strong></label>
                            <div class="amenities-checkboxes" style="max-height: 200px; overflow-y: auto;">
                                <c:forEach var="amenity" items="${allAmenities}">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox"
                                               name="amenity" value="${amenity}"
                                               id="amenity_${amenity}"
                                               <c:if test="${selectedAmenities.contains(amenity)}">checked</c:if>>
                                        <label class="form-check-label" for="amenity_${amenity}">
                                            ${amenity}
                                        </label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-success w-100">Применить фильтры</button>
                        <a href="${pageContext.request.contextPath}/map" class="btn btn-outline-secondary w-100 mt-2">
                            Сбросить фильтры
                        </a>
                    </form>
                </div>

                <!-- Список площадок -->
                <div class="sidebar">
                    <h5>🎯 Найдено площадок: ${playgrounds.size()}</h5>
                    <c:forEach var="pg" items="${playgrounds}">
                        <div class="card playground-card" onclick="focusOnMarker(${pg.id})">
                            <div class="card-body">
                                <h6 class="card-title">${pg.name}</h6>
                                <p class="card-text small text-muted mb-2">
                                    📍 ${pg.address}
                                </p>
                                <div class="d-flex justify-content-between align-items-center">
                                    <c:if test="${pg.avgSafetyRating != null}">
                                        <div>
                                            <span class="text-warning">★★★★★</span>
                                            <small class="text-muted ms-1">${pg.avgSafetyRating}/5</small>
                                        </div>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/playground?id=${pg.id}"
                                       class="btn btn-sm btn-outline-primary">Подробнее</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Карта -->
            <div class="col-md-9">
                <div id="map"></div>
                <div class="mt-3">
                    <button class="btn btn-sm btn-primary me-2" onclick="showAllMarkers()">
                        🔍 Показать все
                    </button>
                    <button class="btn btn-sm btn-info me-2" onclick="locateMe()">
                        📍 Мое местоположение
                    </button>
                    <span class="float-end text-muted">
                        ${playgrounds.size()} площадок на карте
                    </span>
                </div>
            </div>
        </div>
    </div>

    <!-- Скрипты -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script>
        // Инициализация карты
        const map = L.map('map').setView([59.93, 30.31], 12);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap'
        }).addTo(map);

        // Маркеры из JSON
        const playgrounds = ${jsonPlaygrounds};
        const markers = {};

        // Добавляем маркеры
        playgrounds.forEach(pg => {
            const marker = L.marker([pg.lat, pg.lng])
                .bindPopup(`
                    <b>${pg.name}</b><br>
                    ${pg.address}<br>
                    <a href="${pageContext.request.contextPath}/playground?id=${pg.id}"
                       class="btn btn-sm btn-primary mt-2">Подробнее</a>
                `)
                .addTo(map);
            markers[pg.id] = marker;
        });

        // Функция для фокуса на маркере
        window.focusOnMarker = function(id) {
            const marker = markers[id];
            if (marker) {
                map.setView(marker.getLatLng(), 16);
                marker.openPopup();
            }
        };

        window.showAllMarkers = function() {
            if (Object.keys(markers).length > 0) {
                const group = new L.featureGroup(Object.values(markers));
                map.fitBounds(group.getBounds().pad(0.1));
            }
        };

        window.locateMe = function() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(position => {
                    const { latitude, longitude } = position.coords;
                    map.setView([latitude, longitude], 15);
                    L.marker([latitude, longitude])
                        .addTo(map)
                        .bindPopup("Вы здесь")
                        .openPopup();
                });
            }
        };

        // Функция выхода
        function confirmLogout() {
            if (confirm('Вы действительно хотите выйти?')) {
                fetch('${pageContext.request.contextPath}/logout', {
                    method: 'POST'
                }).then(() => {
                    window.location.href = '${pageContext.request.contextPath}/index.html';
                });
            }
            return false;
        }

        // Показываем все маркеры при загрузке
        setTimeout(showAllMarkers, 500);
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>