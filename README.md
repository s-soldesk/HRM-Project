# Spring Boot 기반 인사관리 프로젝트

## 📝 프로젝트 소개
'관리자', '인사부원', '일반사원' 권한별 로그인을 통한 차별화된 인사관리 웹페이지입니다. 사원 정보 관리부터 근태, 급여, 메시지, 일정, 공지사항까지 기업 운영에 필요한 기능을 제공합니다.

## ⚡️ 주요 기능
### 1. 일정관리
- 사원이 일정을 추가, 수정, 삭제할 수 있는 캘린더 시스템
- 일정공유를 통해 부서 간 효율적인 일정 조율 가능
- 전체 일정과 개인 일정을 구분하여 관리
- 확정된 휴가는 자동으로 캘린더에 반영되어 팀원 간 공유

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
- 사용자들 간 메시지를 통한 실시간 커뮤니케이션
- 개인 채팅방을 통한 1:1 대화 기능
- 읽음/안읽음 상태 표시 기능
- 메시지 전송 시간 기록 및 표시

### 5. 사원관리
- 인사부원이 사원정보를 입력하여 사원을 추가 및 조회, 수정
- 사원 추가 시 부서별 차등화된 권한 부여
- 카테고리(id, 이름, 부서)별 검색을 통한 사원 조회
- 페이지네이션을 통한 효율적인 사원 목록 관리

### 6. 채용공고 게시판
- 스케줄러를 통한 채용 마감일 관리
- 채용 공고 상태(진행 중, 마감 등) 자동 업데이트
- 인사부원 및 관리자만 채용공고 작성 및 관리 가능

### 7. 공지사항
- 관리자 권한으로 회사 전체 공지사항 작성 및 관리
- 조회수 트래킹을 통한 공지사항 확인 현황 파악
- 검색 기능으로 과거 공지사항 빠르게 조회
- 공지사항 수정 및 삭제 기능

### 8. 프로필 관리
- 개인 정보 조회 및 프로필 이미지 변경 기능
- 비밀번호 변경 기능으로 계정 보안 강화
- 관리자는 별도의 관리자 프로필 제공

## 🛠 기술 스택
- **Backend**: Java, Spring boot
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: MySQL
- **Tools**: GitHub


## 📊 데이터베이스 구조
### Employee
- 사원 정보 (사원번호, 이름, 이메일, 전화번호, 입사일, 부서, 직급 등)

### Department
- 부서 정보 (부서번호, 부서명, 위치)

### Recruitment_post
- 채용 게시판 게시글 정보 (공고ID, 제목, 내용, 작성자, 마감일, 상태 등)

### Salary
- 사원 및 날자별 급여 정보 (급여ID, 사원번호, 지급일, 기본급, 수당, 총액 등)

### Attendance
- 사원의 출/퇴근 기록 정보 (출근시간, 퇴근시간, 근무시간, 상태 등)

### UserAccount 
- 사용자 계정 정보 (이메일, 비밀번호, 권한 등)

### Schedule
- 일정 관리 정보 (일정ID, 사원번호, 제목, 시작일, 종료일 등)

### Message
- 메시지 정보 (메시지ID, 발신자, 수신자, 내용, 전송시간, 읽음상태 등)

### Notice
- 공지사항 정보 (공지ID, 제목, 내용, 작성자, 작성일, 조회수 등)

  

## 🏄🏻‍♂️ ER다이어그램
<img width="571" alt="스크린샷 2025-03-08 오후 3 43 55" src="https://github.com/user-attachments/assets/e71295f9-e8fc-40b1-891d-9a952937c046" />


## 🔍 주요 API 엔드포인트
```
근태 관리
GET /attendance - 근태 관리 메인 페이지
GET /attendance/records - 근태 기록 조회 페이지
GET /attendance/update/{attendanceId} - 근태 기록 수정 페이지
POST /attendance/update - 근태 기록 수정 처리
GET /attendance/today/status - 오늘의 출퇴근 상태 조회 

출퇴근 관리
POST /attendance/commute/check_in - 출근 기록
POST /attendance/commute/check_out - 퇴근 기록

휴가 관리
GET /attendance/leave/add - 휴가 신청 페이지
POST /attendance/leave/add - 휴가 신청 처리
GET /attendance/leave/list - 휴가 신청 목록 페이지
POST /attendance/leave/approve - 휴가 승인 처리 (HR 관리자용)
POST /attendance/leave/reject - 휴가 거절 처리 (HR 관리자용)
POST /attendance/leave/delete/{leaveId} - 휴가 삭제

사원 관리
GET /employees - 사원 관리 페이지
GET /employees/list - 사원 리스트 조회 API
GET /employees/{employeeId} - 사원 세부정보 조회 API
GET /employees/department/list - 부서 목록 조회 API
POST /employees/add - 사원 추가 API
PUT /employees/{employeeId} - 사원 정보 수정 API
GET /employees/search - 사원 검색 API

급여 관리
GET /salary - 급여 메인 페이지 (권한에 따른 리다이렉션)
GET /salary/manage - 전체 사원 급여 조회 (인사팀 전용)
GET /salary/employee - 개인 급여 조회 (일반 사원)
GET /salary/detail/{salaryId} - 급여 명세서 상세 조회

급여 계산
GET /salary/calculate - 급여 계산 페이지
GET /salary/calculate/status/{yearMonth} - 특정 월 급여 계산 현황 조회
GET /salary/calculate/detail/{employeeId}/{yearMonth} - 사원별 월간 상세 페이지
POST /salary/calculate/confirm/{employeeId}/{yearMonth} - 근태 확정 API
POST /salary/calculate/salary/{employeeId}/{yearMonth} - 급여 계산 API
POST /salary/calculate/initialize/{employeeId}/{yearMonth} - 급여데이터 임의 초기화 API
GET /salary/manual-edit/{employeeId}/{yearMonth} - 급여 수동 편집 폼
POST /salary/manual-edit/save - 급여 수동 편집 저장

일정 관리
GET /schedule - 일정 페이지
GET /api/schedules - 모든 직원의 일정 조회 API
POST /api/schedules/add - 일정 추가 API
DELETE /api/schedules/delete/{scheduleId} - 일정 삭제 API
PUT /api/schedules/update/{scheduleId} - 일정 수정 API

공지사항
GET /notices - 공지사항 목록 페이지
GET /notices/{id} - 공지사항 상세 조회
GET /notices/new - 공지사항 작성 폼
POST /notices - 공지사항 생성
GET /notices/{id}/edit - 공지사항 수정 폼
POST /notices/{id} - 공지사항 수정 처리
POST /notices/{id}/delete - 공지사항 삭제
GET /notices/search - 공지사항 검색

메시지
GET /messages - 메시지 목록 페이지
GET /messages/chat/{userId} - 특정 사용자와의 채팅방
POST /messages/send - 메시지 전송

채용 공고
GET /recruitments - 채용 공고 목록
GET /recruitments/{id} - 채용 공고 상세
GET /recruitments/add - 채용 공고 작성 폼
POST /recruitments/add - 채용 공고 생성
GET /recruitments/edit/{id} - 채용 공고 수정 폼
PUT /recruitments/edit/{id} - 채용 공고 수정
DELETE /recruitments/{id} - 채용 공고 삭제

프로필 관리
GET /profile - 프로필 조회
POST /profile/update - 프로필 업데이트
POST /profile/password - 비밀번호 변경
```

## 🏄🏻‍♂️ 주요 기능별 처리 흐름

1. **사원 추가**
  - 인사부원이 신규 사원을 추가 -> 아이디는 email, 비밀번호는 1234로 초기화
  - 사원 추가 시 직급, 부서, 입사일 등 기본 정보 설정
  - 급여 기본 정보 자동 생성

2. **권한별 로그인**
  - 추가된 사원이 아이디, 비밀번호를 통해 로그인 → 권한별 차등화된 페이지 표시
  - 관리자: 모든 기능 접근 가능
  - 인사부원: 사원관리, 근태관리, 급여관리, 채용관리 접근 가능
  - 일반사원: 개인 근태관리, 개인 급여 조회, 일정관리, 메시지 기능 접근 가능

3. **근태 관리**
  - 출근/퇴근 버튼 클릭 → DB에 시간 기록 → 출퇴근 상태 업데이트
  - 근태 조회 시 권한에 따라 전체/개인 조회 분기 처리
  - 휴가 신청 → 인사부원 승인/거절 → 처리 상태 업데이트

4. **급여 계산**
  - 인사부원이 특정 월 선택 → 사원별 근태 기록 확인 → 급여 계산 실행
  - 기본급 + 수당(식대, 직책수당, 초과근무수당) - 공제액(세금, 보험 등) = 실 지급액
  - 급여 확정 후 사원별 급여명세서 생성

5. **일정 관리**
  - 일정 추가 → 시작일/종료일 설정 → 캘린더에 표시
  - 본인 일정만 수정/삭제 가능
  - 휴가 승인 시 캘린더에 자동 반영

6. **메시지 기능**
  - 발신자가 수신자 선택 → 메시지 작성 → 전송
  - 수신자는 실시간으로 메시지 확인 가능
  - 읽음 상태 자동 갱신

7. **공지사항 관리**
  - 관리자가 공지사항 작성 → 전체 사원에게 공개
  - 사원들이 공지사항 조회 시 조회수 증가
  - 관리자만 공지사항 수정/삭제 가능

8. **채용 관리**
  - 인사부원이 채용공고 작성 → 게시 → 마감일 설정
  - 마감일 도래 시 자동으로 상태 변경
  - 인사부원은 언제든 공고 수정/삭제 가능
