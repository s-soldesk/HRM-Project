document.addEventListener('DOMContentLoaded', function() {
    // 미니 캘린더 초기화
    let calendarEl = document.getElementById('mini-calendar');
    if (calendarEl) {
        let calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            height: 'auto',
            headerToolbar: {
                left: 'prev',
                center: 'title',
                right: 'next'
            },
            navLinks: true,
            dayMaxEvents: 2,
            eventTimeFormat: {
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            },
            // 서버에서 일정 가져오기
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
					},
        });
        
        calendar.render();
    }

    // 출퇴근 시간 업데이트
    function updateAttendanceTime() {
        $.ajax({
            url: '/api/attendance/today',
            type: 'GET',
            success: function(data) {
                if (data.checkInTime) {
                    $('#checkInTime').text(formatTime(data.checkInTime));
                }
                if (data.checkOutTime) {
                    $('#checkOutTime').text(formatTime(data.checkOutTime));
                }
            },
            error: function(error) {
                console.error('Error fetching attendance time:', error);
            }
        });
    }

    // 시간 포맷팅 헬퍼 함수
    function formatTime(timeStr) {
        const date = new Date(timeStr);
        return date.toLocaleTimeString('ko-KR', { 
            hour: '2-digit', 
            minute: '2-digit',
            hour12: false
        });
    }

    // 초기 데이터 로드
    updateAttendanceTime();
});