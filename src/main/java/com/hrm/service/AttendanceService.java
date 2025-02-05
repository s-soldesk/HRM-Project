package com.hrm.service;

import com.hrm.dao.AttendanceDao;
import com.hrm.dto.AttendanceDto;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceService {

	@Autowired
	private AttendanceDao attendanceDao;

	// 근태 기록 조회
	public List<AttendanceDto> searchAttendanceRecords(String employeeId, String name, String startDate, String endDate, String attendanceType) {
        return attendanceDao.searchAttendanceRecords(employeeId, name, startDate, endDate, attendanceType);
    }
	
	// 특정한 근태 기록과 해당 사원 이름 조회
	public AttendanceDto getAttendanceById(int attendanceId) {
        return attendanceDao.getAttendanceById(attendanceId);
    }

    // 근태 기록 수정
    public void updateAttendance(AttendanceDto attendance) {
        attendanceDao.updateAttendance(attendance);
    }
    
    // PDF 생성 메서드
    public byte[] generatePdf(List<AttendanceDto> records) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // 한글 폰트 설정
            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/NanumGothic.ttf", PdfEncodings.IDENTITY_H);

            // 문서 제목 추가
            Paragraph title = new Paragraph("근태 기록 보고서")
                    .setFont(font)
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // 테이블 생성
            Table table = new Table(new float[]{1.5f, 2.5f, 2f, 2f, 2f, 1.5f, 3f});
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(10);

            // 헤더 스타일 설정
            Cell headerStyle = new Cell().setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(59, 130, 246))  // Blue header color
                                         .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
                                         .setFont(font)
                                         .setBold()
                                         .setPadding(5)
                                         .setTextAlignment(TextAlignment.CENTER);

            // 헤더 추가
            table.addHeaderCell(headerStyle.clone(false).add(new Paragraph("사원 ID")));
            table.addHeaderCell(headerStyle.clone(false).add(new Paragraph("사원 이름")));
            table.addHeaderCell(headerStyle.clone(false).add(new Paragraph("날짜")));
            table.addHeaderCell(headerStyle.clone(false).add(new Paragraph("출근 시간")));
            table.addHeaderCell(headerStyle.clone(false).add(new Paragraph("퇴근 시간")));
            table.addHeaderCell(headerStyle.clone(false).add(new Paragraph("근태 유형")));
            table.addHeaderCell(headerStyle.clone(false).add(new Paragraph("비고")));

            // 날짜와 시간 포맷 설정
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // 데이터 추가
            for (AttendanceDto record : records) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(record.getEmployeeId())))
                                         .setFont(font)
                                         .setPadding(5)
                                         .setTextAlignment(TextAlignment.CENTER));

                table.addCell(new Cell().add(new Paragraph(record.getEmployeeName() != null ? record.getEmployeeName() : "N/A"))
                                         .setFont(font)
                                         .setPadding(5));

                table.addCell(new Cell().add(new Paragraph(record.getDate() != null ? record.getDate().format(dateFormatter) : "N/A"))
                                         .setFont(font)
                                         .setPadding(5));

                table.addCell(new Cell().add(new Paragraph(record.getCheckInTime() != null ? record.getCheckInTime().format(timeFormatter) : "N/A"))
                                         .setFont(font)
                                         .setPadding(5));

                table.addCell(new Cell().add(new Paragraph(record.getCheckOutTime() != null ? record.getCheckOutTime().format(timeFormatter) : "N/A"))
                                         .setFont(font)
                                         .setPadding(5));

                table.addCell(new Cell().add(new Paragraph(record.getAttendanceType() != null ? record.getAttendanceType() : "N/A"))
                                         .setFont(font)
                                         .setPadding(5)
                                         .setTextAlignment(TextAlignment.CENTER));

                table.addCell(new Cell().add(new Paragraph(record.getRemarks() != null ? record.getRemarks() : "N/A"))
                                         .setFont(font)
                                         .setPadding(5));
            }

            // 문서에 테이블 추가 및 닫기
            document.add(table);
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("PDF 생성 중 오류 발생", e);
        }
    }

    
}
