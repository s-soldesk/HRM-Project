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
					let events = response.map(schedule => {
						let eventObj = {
							id: schedule.scheduleId,
							title: schedule.title + ` (직원: ${schedule.employeeId})`,
							start: schedule.startDate,
							end: schedule.endDate,
							allDay: schedule.allDay
						};

						// ✅ 휴가는 승인된(CONFIRMED) 상태일 때만 표시
						if (schedule.type === "Leave" && schedule.status !== "CONFIRMED") {
							return null; // 승인되지 않은 휴가는 표시 안 함
						}

						return eventObj;
					}).filter(event => event !== null); // `null` 값 제거

					successCallback(events);
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
                let isAllDay = arg.allDay; // ✅ 종일 여부 반영
                let newEvent = {
                    title: title,
                    start: arg.startStr,
                    end: arg.endStr,
                    allDay: isAllDay // ✅ allDay 값 서버로 전송
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
                            allDay: data.allDay // ✅ 서버에서 받은 allDay 반영
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
                end: info.event.end ? info.event.end.toISOString() : info.event.start.toISOString(),
                allDay: info.event.allDay // ✅ 이동 시 allDay 값 유지
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
                end: info.event.end ? info.event.end.toISOString() : info.event.start.toISOString(),
                allDay: info.event.allDay // ✅ 크기 조정 시 allDay 값 유지
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
			let userRole = $("meta[name='user-role']").attr("content"); // HTML meta 태그로 역할 가져오기

			if (confirm("이 일정을 삭제하시겠습니까?")) {
				$.ajax({
					url: "/api/schedules/delete/" + info.event.id,
					type: "DELETE",
					success: function(response) {
						if (response === "success") {
							alert("일정이 삭제되었습니다.");
							info.event.remove();
						} else if (response === "forbidden") {
							alert("승인된 휴가는 HR 또는 관리자만 삭제할 수 있습니다.");
						} else {
							alert("일정 삭제에 실패했습니다.");
						}
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