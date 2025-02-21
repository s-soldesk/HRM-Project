document.addEventListener('DOMContentLoaded', function() {
    // 미니 캘린더 초기화
    let calendarEl = document.getElementById('mini-calendar');
    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        height: 'auto',
        headerToolbar: {
            left: 'prev',
            center: 'title',
            right: 'next'
        },
        eventSources: [{
            url: '/api/schedules',
            method: 'GET'
        }],
        eventDisplay: 'block',
        dayMaxEvents: 2,
        eventTimeFormat: {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        }
    });
    calendar.render();

    // 출퇴근 시간 업데이트
    function updateAttendanceTime() {
        fetch('/api/attendance/today')
            .then(response => response.json())
            .then(data => {
                if (data.checkInTime) {
                    document.getElementById('checkInTime').textContent = 
                        new Date(data.checkInTime).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
                }
                if (data.checkOutTime) {
                    document.getElementById('checkOutTime').textContent = 
                        new Date(data.checkOutTime).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
                }
            })
            .catch(error => console.error('Error:', error));
    }

    // 초기 데이터 로드
    updateAttendanceTime();
    
    // 1분마다 출퇴근 시간 업데이트
    setInterval(updateAttendanceTime, 60000);
});