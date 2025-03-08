# Spring Boot 기반 인사관리 프로젝트

## 📝 프로젝트 소개
'관리자', '인사부원', '일반사원' 권한별 로그인을 통한 차별화된 인사관리 웹페이지

## ⚡️ 주요 기능
### 1. 일정관리
- 사원이 일정을 ~
- 일정공유 ~

### 2. 근태관리
- 직원들이 ‘출근’ 및 ‘퇴근’ 버튼을 통해 자신의 출퇴근 시간을 실시간으로 기록
- 일반 사원은 본인의 근태 기록만 이름, 사원번호, 특정 기간 동안별로 조회
- 인사 관리자는 모든 사원의 근태 기록을 이름, 사원번호, 특정 기간 동안별로 조회 가능, 필요한 경우 데이터를 수정 가능
- 직원들은 ‘휴가 신청’ 버튼을 통해 휴가 신청서를 작성하고 제출할 수 있으며, 제출된 신청 내역과 처리 상태를 ‘휴가 신청 조회’ 기능을 통해 확인

### 3. 급여관리
- 일반 사원은 로그인한 계정과 EmployeeId를 매핑하여 본인 급여 지급 내역만 조회
- 인사 부원은 모든 사원의 급여 정보를 이름, 사원번호, 직급별로 조회
- 인사 부원은 근태 기록에 따른 자동 급여 계산 가능 (필요시 수기 수정 가능)
  

### 4. 메시지
- 사용자들 간 메시지 ~

### 5. 사원관리
- 인사부원이 사원정보를 입력하여 사원을 추가 및 조회, 수정
- 사원 추가 시 부서별 차등화된 권한 부여
- 카테고리(id, 이름, 부서)별 검색을 통한 사원 조회

### 6. 채용공고 게시판
- 스케줄러를 통한 채용 마감일 관리


## 🛠 기술 스택
- **Backend**: Java, Spring boot
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: MySQL
- **Tools**: GitHub


## 📊 데이터베이스 구조
### Employee
- 사원 정보

### Department
- 부서 정보

### Recruitment_post
- 채용 게시판 게시글 정보

### Salary
- 사원 및 날자별 급여 정보

### Attendance
- 사원의 출/퇴근 기록 정보

### 나머지들 
~
~

## 🏄🏻‍♂️ ER다이어그램
<img width="571" alt="스크린샷 2025-03-08 오후 3 43 55" src="https://github.com/user-attachments/assets/e71295f9-e8fc-40b1-891d-9a952937c046" />


## 🔍 주요 API 엔드포인트
```
## 근태 관리
GET /attendance - 근태 관리 메인 페이지
GET /attendance/records - 근태 기록 조회 페이지
GET /attendance/update/{attendanceId} - 근태 기록 수정 페이지
POST /attendance/update - 근태 기록 수정 처리
GET /attendance/today/status - 오늘의 출퇴근 상태 조회 

### 출퇴근 관리
POST /attendance/commute/check_in - 출근 기록
POST /attendance/commute/check_out - 퇴근 기록

### 휴가 관리
GET /attendance/leave/add - 휴가 신청 페이지
POST /attendance/leave/add - 휴가 신청 처리
GET /attendance/leave/list - 휴가 신청 목록 페이지
POST /attendance/leave/approve - 휴가 승인 처리 (HR 관리자용)
POST /attendance/leave/reject - 휴가 거절 처리 (HR 관리자용)
POST /attendance/leave/delete/{leaveId} - 휴가 삭제

## 사원 관리
GET /employees - 사원 관리 페이지
GET /employees/list - 사원 리스트 조회 API
GET /employees/{employeeId} - 사원 세부정보 조회 API
GET /employees/department/list - 부서 목록 조회 API
POST /employees/add - 사원 추가 API
PUT /employees/{employeeId} - 사원 정보 수정 API
GET /employees/search - 사원 검색 API

## 급여 관리
GET /salary - 급여 메인 페이지 (권한에 따른 리다이렉션)
GET /salary/manage - 전체 사원 급여 조회 (인사팀 전용)
GET /salary/employee - 개인 급여 조회 (일반 사원)
GET /salary/detail/{salaryId} - 급여 명세서 상세 조회

### 급여 계산
GET /salary/calculate - 급여 계산 페이지
GET /salary/calculate/status/{yearMonth} - 특정 월 급여 계산 현황 조회
GET /salary/calculate/detail/{employeeId}/{yearMonth} - 사원별 월간 상세 페이지
POST /salary/calculate/confirm/{employeeId}/{yearMonth} - 근태 확정 API
POST /salary/calculate/salary/{employeeId}/{yearMonth} - 급여 계산 API
POST /salary/calculate/initialize/{employeeId}/{yearMonth} - 급여데이터 임의 초기화 API
GET /salary/manual-edit/{employeeId}/{yearMonth} - 급여 수동 편집 폼
POST /salary/manual-edit/save - 급여 수동 편집 저장

## 일정 관리
GET /schedule - 일정 페이지
GET /api/schedules - 모든 직원의 일정 조회 API
POST /api/schedules/add - 일정 추가 API
DELETE /api/schedules/delete/{scheduleId} - 일정 삭제 API
PUT /api/schedules/update/{scheduleId} - 일정 수정 API

## 공지사항
GET /notices - 공지사항 목록 페이지
GET /notices/{id} - 공지사항 상세 조회
GET /notices/new - 공지사항 작성 폼
POST /notices - 공지사항 생성
GET /notices/{id}/edit - 공지사항 수정 폼
POST /notices/{id} - 공지사항 수정 처리
POST /notices/{id}/delete - 공지사항 삭제
GET /notices/search - 공지사항 검색

## 메시지
GET /messages - 메시지 목록 페이지
GET /messages/chat/{userId} - 특정 사용자와의 채팅방
POST /messages/send - 메시지 전송

## 채용 공고
GET /recruitments - 채용 공고 목록
GET /recruitments/{id} - 채용 공고 상세
GET /recruitments/add - 채용 공고 작성 폼
POST /recruitments/add - 채용 공고 생성
GET /recruitments/edit/{id} - 채용 공고 수정 폼
PUT /recruitments/edit/{id} - 채용 공고 수정
DELETE /recruitments/{id} - 채용 공고 삭제

## 프로필 관리
GET /profile - 프로필 조회
POST /profile/update - 프로필 업데이트
POST /profile/password - 비밀번호 변경
```

## 🏄🏻‍♂️ 주요 기능별 처리 흐름

1. **사원 추가**
   - 인사부원이 신규 사원을 추가 -> 아이디는 email, 비밀번호는 1234로 초기화

2. **권한별 로그인**
   - 추가된 사원이 아이디, 비밀번호를 통해 로그인 → 권한별 차등화된 페이지 표시

3. ****
   - 
