document.addEventListener('DOMContentLoaded', function() {
    // 애니메이션 클래스 추가 함수
    function addAnimationClass(element, className) {
        element.classList.add(className);
        element.addEventListener('animationend', () => {
            element.classList.remove(className);
        });
    }

    // 시간 업데이트 함수
    function updateDateTime() {
        const now = new Date();
        const timeElement = document.getElementById('currentTime');
        const dateElement = document.getElementById('currentDate');

        // 날짜 포맷팅
        const dateOptions = {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            weekday: 'long'
        };
        const dateStr = now.toLocaleDateString('ko-KR', dateOptions);

        // 시간 포맷팅
        const timeOptions = {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        };
        const timeStr = now.toLocaleTimeString('ko-KR', timeOptions);

        // 직접 업데이트
        timeElement.textContent = timeStr;
        dateElement.textContent = dateStr;
    }

    // 출퇴근 상태 업데이트 함수
    function updateAttendanceStatus() {
        fetch('/attendance/today/status')
            .then(response => response.json())
            .then(data => {
                const statusElement = document.getElementById('attendanceStatus');
                const checkInElement = document.getElementById('checkInTime');
                const checkOutElement = document.getElementById('checkOutTime');

                // 상태 업데이트
                if (data.checkInTime) {
                    checkInElement.textContent = data.checkInTime.substring(0, 5); // HH:MM 형태로 표시
                    
                    if (data.checkOutTime) {
                        statusElement.textContent = '퇴근';
                        statusElement.className = 'badge bg-secondary';
                        checkOutElement.textContent = data.checkOutTime.substring(0, 5);
                    } else {
                        statusElement.textContent = '근무중';
                        statusElement.className = 'badge bg-success';
                        checkOutElement.textContent = '--:--';
                    }
                } else {
                    statusElement.textContent = '미출근';
                    statusElement.className = 'badge bg-danger';
                    checkInElement.textContent = '--:--';
                    checkOutElement.textContent = '--:--';
                }
            })
            .catch(error => {
                console.error('Error fetching attendance status:', error);
            });
    }

    // 캘린더 초기화
    const calendarEl = document.getElementById('mini-calendar');
    if (calendarEl) {
        const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev',
                center: 'title',
                right: 'next'
            },
            height: 'auto',
            contentHeight: 'auto',
            aspectRatio: 1.5,
            dayMaxEvents: true,
            eventDidMount: function(info) {
                // Tippy.js 툴팁 설정
                tippy(info.el, {
                    content: info.event.title,
                    placement: 'top',
                    animation: 'shift-away',
                    theme: 'light-border'
                });
            },
            events: function(fetchInfo, successCallback, failureCallback) {
                $.ajax({
                    url: "/api/schedules",
                    type: "GET",
                    dataType: "json",
                    success: function(response) {
                        let eventsMap = new Map();

                        response.forEach(schedule => {
                            let scheduleId = schedule.scheduleId;
                            let employeeInfo = `직원: ${schedule.employeeId}`;

                            // 시간 정보 포맷팅
                            let timePrefix = '';
                            if (!schedule.allDay) {
                                let startTime = new Date(schedule.startDate);
                                let hours = startTime.getHours();
                                let period = hours < 12 ? '오전' : '오후';
                                hours = hours % 12 || 12;
                                timePrefix = `${period} ${hours}시 `;
                            }

                            if (eventsMap.has(scheduleId)) {
                                let existingEvent = eventsMap.get(scheduleId);
                                if (!existingEvent.title.includes(employeeInfo)) {
                                    existingEvent.title += `, ${employeeInfo}`;
                                }
                            } else {
                                eventsMap.set(scheduleId, {
                                    id: scheduleId,
                                    title: `${timePrefix}${schedule.title} (${employeeInfo})`,
                                    start: schedule.startDate,
                                    end: schedule.endDate,
                                    allDay: schedule.allDay
                                });
                            }
                        });

                        successCallback(Array.from(eventsMap.values()));
                    },
                    error: function(xhr, status, error) {
                        console.error("🚨 일정 불러오기 실패:", error);
                        failureCallback(error);
                    }
                });
            }
        });

        calendar.render();

        // 반응형 처리
        window.addEventListener('resize', () => {
            calendar.updateSize();
        });
    }

    // 출퇴근 버튼 이벤트 리스너 추가
    const checkInBtn = document.getElementById('checkInBtn');
    const checkOutBtn = document.getElementById('checkOutBtn');
    
    if (checkInBtn) {
        checkInBtn.addEventListener('click', function() {
            fetch('/attendance/commute/check_in', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                // 상태 즉시 갱신
                updateAttendanceStatus();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('출근 처리 중 오류가 발생했습니다.');
            });
        });
    }
    
    if (checkOutBtn) {
        checkOutBtn.addEventListener('click', function() {
            fetch('/attendance/commute/check_out', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                // 상태 즉시 갱신
                updateAttendanceStatus();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('퇴근 처리 중 오류가 발생했습니다.');
            });
        });
    }

    // 스크롤 애니메이션
    const observerOptions = {
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-slide-up');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.section-box').forEach(box => {
        observer.observe(box);
    });

    // 초기화
    updateDateTime();
    updateAttendanceStatus();

    // 주기적 업데이트
    setInterval(updateDateTime, 1000);
    setInterval(updateAttendanceStatus, 60000);
});