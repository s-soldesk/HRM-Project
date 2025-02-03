package com.hrm.controller;

import com.hrm.dto.AttendanceDto;
import com.hrm.service.AttendanceService;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 근태 관리 메인 페이지
    @GetMapping
    public String showAttendanceMainPage() {
        return "attendance";
    }
    
    
    // 근태 기록 조회 페이지
    @GetMapping("/records")
    public String getRecordsPage(@RequestParam(value = "employeeId", required = false) String employeeId,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "startDate", required = false) String startDate,
                                 @RequestParam(value = "endDate", required = false) String endDate,
                                 @RequestParam(value = "attendanceType", required = false) String attendanceType,
                                 Model model) {
    	
    	// 빈 문자열을 null로 변환
        if (startDate != null && startDate.isEmpty()) startDate = null;
        if (endDate != null && endDate.isEmpty()) endDate = null;
        if (employeeId != null && employeeId.isEmpty()) employeeId = null;
        if (name != null && name.isEmpty()) name = null;
        if (attendanceType != null && attendanceType.isEmpty()) attendanceType = null;
    	
        List<AttendanceDto> records = attendanceService.searchAttendanceRecords(employeeId, name, startDate, endDate, attendanceType);
        model.addAttribute("records", records);
        return "records";
    }
    
    // 근태 기록 수정 페이지
    @GetMapping("/update/{attendanceId}")
    public String getUpdatePage(@PathVariable("attendanceId") int attendanceId, Model model) {
        AttendanceDto attendance = attendanceService.getAttendanceById(attendanceId);
        if (attendance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "근태 기록을 찾지 못했습니다");
        }
        model.addAttribute("attendance", attendance);
        return "update";
    }

    // 근태 기록 수정 처리
    @PostMapping("/update")
    public String updateAttendance(@ModelAttribute AttendanceDto attendance) {
        attendanceService.updateAttendance(attendance);
        return "redirect:/attendance/records";
    }
    
    // PDF 다운로드 메서드 추가
    @GetMapping("/records/pdf")
    public ResponseEntity<byte[]> generatePdfReport(
            @RequestParam(value = "employeeId", required = false) String employeeId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "attendanceType", required = false) String attendanceType) {
    	
    	// 빈 문자열을 null로 변환
        if (startDate != null && startDate.isEmpty()) startDate = null;
        if (endDate != null && endDate.isEmpty()) endDate = null;
        if (employeeId != null && employeeId.isEmpty()) employeeId = null;
        if (name != null && name.isEmpty()) name = null;
        if (attendanceType != null && attendanceType.isEmpty()) attendanceType = null;

        // 근태 기록 조회
        List<AttendanceDto> records = attendanceService.searchAttendanceRecords(employeeId, name, startDate, endDate, attendanceType);

        // PDF 생성
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // 문서 제목
        document.add(new Paragraph("근태 기록 보고서").setBold().setFontSize(16));

        try {
            // 한글 폰트 설정
            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/NanumGothic.ttf", PdfEncodings.IDENTITY_H);

            // 테이블 생성 및 폰트 적용
            Table table = new Table(new float[]{100F, 100F, 100F, 100F, 100F, 100F, 150F});
            table.setWidth(UnitValue.createPercentValue(100));  // 테이블 너비를 페이지에 맞춤

            // 테이블 헤더 추가
            table.addHeaderCell(new Cell().add(new Paragraph("사원 ID").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("사원 이름").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("날짜").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("출근 시간").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("퇴근 시간").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("근태 유형").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("비고").setFont(font)));

            // 테이블 데이터 추가 로직...
            
         // 날짜 형식 지정 (예: yyyy-MM-dd)
    		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // 시간 형식 지정 (예: HH:mm)
    		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    		// 테이블에 데이터 추가
    		for (AttendanceDto record : records) {
    		    table.addCell(new Cell().add(new Paragraph(String.valueOf(record.getEmployeeId()))));
    		    
    		    // 사원 이름이 null인 경우 처리
    		    String employeeName = (record.getEmployeeName() != null) ? record.getEmployeeName() : "N/A";
    		    table.addCell(new Cell().add(new Paragraph(employeeName)));

    		    // 날짜가 null인 경우 처리
    		    String formattedDate = (record.getDate() != null) 
    		                           ? record.getDate().format(dateFormatter) 
    		                           : "N/A";
    		    table.addCell(new Cell().add(new Paragraph(formattedDate)));

    		    // 출근 시간과 퇴근 시간 null 처리
    		    String formattedCheckInTime = (record.getCheckInTime() != null)
    		                                  ? record.getCheckInTime().format(timeFormatter)
    		                                  : "N/A";
    		    table.addCell(new Cell().add(new Paragraph(formattedCheckInTime)));

    		    String formattedCheckOutTime = (record.getCheckOutTime() != null)
    		                                   ? record.getCheckOutTime().format(timeFormatter)
    		                                   : "N/A";
    		    table.addCell(new Cell().add(new Paragraph(formattedCheckOutTime)));

    		    // 근태 유형 및 비고 null 처리
    		    String formattedAttendanceType = (record.getAttendanceType() != null) ? record.getAttendanceType() : "N/A";
    		    table.addCell(new Cell().add(new Paragraph(formattedAttendanceType)));

    		    String formattedRemarks = (record.getRemarks() != null) ? record.getRemarks() : "N/A";
    		    table.addCell(new Cell().add(new Paragraph(formattedRemarks)));

    		}


            // 문서에 테이블 추가 후 닫기
            document.add(table);
            document.close();

            // PDF 파일로 응답 전송
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=attendance_report.pdf");
            headers.add("Content-Type", "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("PDF 생성 중 오류 발생", e);
        }
        
		
    }
}
