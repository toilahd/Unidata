-- BẬT SERVER OUTPUT
SET SERVEROUTPUT ON;

BEGIN

  UPDATE DONVI SET TRGDV = NULL;

  -- Xóa dữ liệu cũ nếu có (đảm bảo không trùng khóa)
  DELETE FROM DANGKY;
  DELETE FROM MOMON;
  DELETE FROM HOCPHAN;
  DELETE FROM SINHVIEN;
  DELETE FROM NHANVIEN;
  DELETE FROM DONVI;

  COMMIT;

  ----------------------------------------------------------
  -- 1. ĐƠN VỊ
  ----------------------------------------------------------
  INSERT INTO DONVI (MADV, TENDV, LOAIDV) VALUES ('CNTT', 'Khoa Công nghệ thông tin', 'Khoa');
  INSERT INTO DONVI (MADV, TENDV, LOAIDV) VALUES ('KTMT', 'Khoa Kỹ thuật máy tính', 'Khoa');
  INSERT INTO DONVI (MADV, TENDV, LOAIDV) VALUES ('TOAN', 'Khoa Toán', 'Khoa');
  INSERT INTO DONVI (MADV, TENDV, LOAIDV) VALUES ('PDT', 'Phòng Đào tạo', 'Phòng');
  INSERT INTO DONVI (MADV, TENDV, LOAIDV) VALUES ('PKT', 'Phòng Kế toán', 'Phòng');
  INSERT INTO DONVI (MADV, TENDV, LOAIDV) VALUES ('TCHC', 'Phòng Tổ chức Hành chính', 'Phòng');
  INSERT INTO DONVI (MADV, TENDV, LOAIDV) VALUES ('CTSV', 'Phòng Công tác sinh viên', 'Phòng');
  ----------------------------------------------------------
  -- 2. NHÂN VIÊN
  ----------------------------------------------------------
  INSERT INTO NHANVIEN VALUES ('GV001', 'Nguyễn Văn A', 'Nam', TO_DATE('1980-05-10','YYYY-MM-DD'), 15000000, 2000000, '0901111222', 'GV', 'CNTT');
  INSERT INTO NHANVIEN VALUES ('GV002', 'Trần Thị B', 'Nữ', TO_DATE('1982-06-20','YYYY-MM-DD'), 14000000, 1500000, '0911111222', 'GV', 'KTMT');
  INSERT INTO NHANVIEN VALUES ('TR001', 'Lê Văn C', 'Nam', TO_DATE('1975-03-15','YYYY-MM-DD'), 18000000, 3000000, '0921111222', 'TRGDV', 'CNTT');
  INSERT INTO NHANVIEN VALUES ('NV001', 'Nguyễn Tùng D', 'Nam', TO_DATE('1985-07-25','YYYY-MM-DD'), 12000000, 1000000, '0931111222', 'NVPDT', 'PDT');
  INSERT INTO NHANVIEN VALUES ('NV002', 'Trần Mỹ E', 'Nữ', TO_DATE('1986-11-30','YYYY-MM-DD'), 12500000, 1100000, '0941111222', 'NVPKT', 'PKT');
  INSERT INTO NHANVIEN VALUES ('NV003', 'Vũ Hồng F', 'Nữ', TO_DATE('1989-04-10','YYYY-MM-DD'), 13000000, 1200000, '0951111222', 'NVTCHC', 'TCHC');
  INSERT INTO NHANVIEN VALUES ('NV004', 'Đào Đức G', 'Nam', TO_DATE('1990-02-02','YYYY-MM-DD'), 11000000, 900000, '0961111222', 'NVCTSV', 'CTSV');
  -- Gán TRGDV cho DONVI
  UPDATE DONVI SET TRGDV = 'TR001' WHERE MADV = 'CNTT';

  ----------------------------------------------------------
  -- 3. SINH VIÊN
  ----------------------------------------------------------
  INSERT INTO SINHVIEN VALUES ('SV001', 'Mai Thanh Hương', 'Nữ', TO_DATE('2003-03-15','YYYY-MM-DD'), 'Q1, HCM', '0901111223', 'CNTT', 'Đang học');
  INSERT INTO SINHVIEN VALUES ('SV002', 'Trần Quốc Bảo', 'Nam', TO_DATE('2002-06-01','YYYY-MM-DD'), 'Q3, HCM', '0901111224', 'CNTT', 'Đang học');
  INSERT INTO SINHVIEN VALUES ('SV003', 'Lê Thị Cẩm', 'Nữ', TO_DATE('2003-07-21','YYYY-MM-DD'), 'Q5, HCM', '0901111225', 'KTMT', 'Bảo lưu');
  INSERT INTO SINHVIEN VALUES ('SV004', 'Nguyễn Văn Duy', 'Nam', TO_DATE('2002-08-18','YYYY-MM-DD'), 'Q7, HCM', '0901111226', 'TOAN', 'Nghỉ học');
  COMMIT;
  ----------------------------------------------------------
  -- 4. HỌC PHẦN
  ----------------------------------------------------------
  INSERT INTO HOCPHAN VALUES ('HP001', 'Cơ sở dữ liệu', 4, 45, 15, 'CNTT');
  INSERT INTO HOCPHAN VALUES ('HP002', 'Kỹ thuật lập trình', 3, 30, 15, 'CNTT');
  INSERT INTO HOCPHAN VALUES ('HP003', 'Toán rời rạc', 3, 45, 0, 'TOAN');

  ----------------------------------------------------------
  -- 5. MỞ MÔN
  ----------------------------------------------------------
  INSERT INTO MOMON VALUES ('MM001', 'HP001', 'GV001', 1, 2024);
  INSERT INTO MOMON VALUES ('MM002', 'HP002', 'GV001', 1, 2024);
  INSERT INTO MOMON VALUES ('MM003', 'HP003', 'GV002', 2, 2024);

  ----------------------------------------------------------
  -- 6. ĐĂNG KÝ
  ----------------------------------------------------------
  INSERT INTO DANGKY VALUES ('SV001', 'MM001', 8.5, 7.5, 8.0, 8.0);
  INSERT INTO DANGKY VALUES ('SV001', 'MM002', 9.0, 9.0, 8.5, 8.8);
  INSERT INTO DANGKY VALUES ('SV002', 'MM001', 7.0, 6.5, 7.0, 6.8);
  INSERT INTO DANGKY VALUES ('SV004', 'MM003', 8.0, 7.5, 7.0, 7.5);

  COMMIT;

  DBMS_OUTPUT.PUT_LINE('ĐÃ SINH DỮ LIỆU MẪU THÀNH CÔNG!');
END;
/

-- Verify the data insertion
SELECT 'DONVI' AS TABLE_NAME, COUNT(*) AS ROW_COUNT FROM DONVI
UNION ALL
SELECT 'NHANVIEN', COUNT(*) FROM NHANVIEN
UNION ALL
SELECT 'SINHVIEN', COUNT(*) FROM SINHVIEN
UNION ALL
SELECT 'HOCPHAN', COUNT(*) FROM HOCPHAN
UNION ALL
SELECT 'MOMON', COUNT(*) FROM MOMON
UNION ALL
SELECT 'DANGKY', COUNT(*) FROM DANGKY;