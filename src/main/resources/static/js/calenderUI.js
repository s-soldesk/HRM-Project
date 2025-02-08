$(document).ready(function () {
    let calendarTag = $('#calendar')[0];
    let calendar = new FullCalendar.Calendar(calendarTag, {
        height: '550px',
        expandRows: true,
        slotMinTime: '00:00',
        slotMaxTime: '23:59',
        headerToolbar: {
            left: 'prevYear,prev,next,nextYear today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
        },
        initialView: 'dayGridMonth',
        navLinks: true,
        editable: true,
        selectable: true,
        nowIndicator: true,
        dayMaxEvents: true,
        locale: 'ko',

        // ✅ 모든 직원의 일정 조회 (중복 방지)
        events: function(fetchInfo, successCallback, failureCallback) {
            $.ajax({
                url: "/api/schedules",
                type: "GET",
                dataType: "json",
                success: function(response) {
                    let events = [];
                    let eventsMap = new Map(); // ✅ `scheduleId` 기준으로 그룹화

                    response.forEach(schedule => {
                        let scheduleId = schedule.scheduleId;
                        let employeeInfo = `직원: ${schedule.employeeId}`;

                        // ✅ 같은 일정이 이미 존재하는지 확인
                        if (eventsMap.has(scheduleId)) {
                            let existingEvent = eventsMap.get(scheduleId);

                            // ✅ 기존 직원 ID가 포함되지 않았다면 추가 (중복 방지)
                            if (!existingEvent.title.includes(employeeInfo)) {
                                existingEvent.title += `, ${employeeInfo}`;
                            }
                        } else {
                            // ✅ 새로운 일정 추가
                            eventsMap.set(scheduleId, {
                                id: scheduleId,
                                title: `${schedule.title} (${employeeInfo})`,
                                start: schedule.startDate,
                                end: schedule.endDate,
                                allDay: true
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

        // ✅ 일정 추가
        select: function(arg) {
            let title = prompt("새로운 일정 제목을 입력하세요:");
            if (title) {
                let newEvent = {
                    title: title,
                    start: arg.startStr,
                    end: arg.endStr
                };

                $.ajax({
                    url: "/api/schedules/add",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(newEvent),
                    success: function(data) {
                        calendar.addEvent({
                            id: data.scheduleId,
                            title: data.title + " (직원: " + data.employeeId + ")",
                            start: data.startDate,
                            end: data.endDate,
                            allDay: true
                        });
                        alert("일정이 추가되었습니다.");
                    },
                    error: function() {
                        alert("일정 추가에 실패했습니다.");
                    }
                });
            }
            calendar.unselect();
        },

        // ✅ 일정 이동 (드래그 & 드롭)
        eventDrop: function(info) {
            let updatedEvent = {
                scheduleId: info.event.id,
                title: info.event.title,
                start: info.event.start ? info.event.start.toISOString() : null,
                end: info.event.end ? info.event.end.toISOString() : info.event.start.toISOString()
            };

            $.ajax({
                url: "/api/schedules/update/" + updatedEvent.scheduleId,
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify(updatedEvent),
                success: function() {
                    alert("일정이 이동되었습니다.");
                },
                error: function(xhr, status, error) {
                    console.error("🚨 이동 실패:", error);
                    alert("일정 이동에 실패했습니다.");
                }
            });
        },

        // ✅ 일정 크기 조정 (Resize)
        eventResize: function(info) {
            let updatedEvent = {
                scheduleId: info.event.id,
                title: info.event.title,
                start: info.event.start.toISOString(),
                end: info.event.end ? info.event.end.toISOString() : info.event.start.toISOString()
            };

            $.ajax({
                url: "/api/schedules/update/" + updatedEvent.scheduleId,
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify(updatedEvent),
                success: function() {
                    alert("일정 시간이 변경되었습니다.");
                },
                error: function(xhr, status, error) {
                    console.error("🚨 크기 조정 실패:", error);
                    alert("일정 크기 변경에 실패했습니다.");
                }
            });
        },

        // ✅ 일정 삭제 (일정 클릭 시)
        eventClick: function(info) {
            if (confirm("이 일정을 삭제하시겠습니까?")) {
                $.ajax({
                    url: "/api/schedules/delete/" + info.event.id,
                    type: "DELETE",
                    success: function() {
                        alert("일정이 삭제되었습니다.");
                        info.event.remove();
                    },
                    error: function(xhr, status, error) {
                        console.error("🚨 삭제 실패:", error);
                        alert("일정 삭제에 실패했습니다.");
                    }
                });
            }
        }
    });

    // ✅ 캘린더 렌더링
    calendar.render();
});
