-- Drop existing views first
BEGIN
    FOR v IN (
        SELECT view_name FROM user_views 
        WHERE view_name IN ('UV_NVCB_CANHAN', 'UV_TRGDV_NV')
    ) LOOP
        EXECUTE IMMEDIATE 'DROP VIEW ' || v.view_name;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN
        NULL; -- Ignore errors if views don't exist
END;
/

-- Drop existing roles
BEGIN
    FOR r IN (
        SELECT role FROM dba_roles 
        WHERE role IN ('RL_NVCB', 'RL_GV', 'RL_NVPDT', 'RL_NVPKT', 'RL_NVTCHC', 'RL_NVCTSV', 'RL_TRDGV', 'RL_SV')
    ) LOOP
        EXECUTE IMMEDIATE 'DROP ROLE ' || r.role;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN
        NULL; -- Ignore errors if roles don't exist
END;
/
-- Yêu cầu 1
-- Câu 1: QUAN HE NHANVIEN
-- Tạo các role cho các vai trò trong hệ thống:
BEGIN
    FOR r IN (
        SELECT role FROM dba_roles 
        WHERE role IN ('RL_NVCB', 'RL_GV', 'RL_NVPDT', 'RL_NVPKT', 'RL_NVTCHC', 'RL_NVCTSV', 'RL_TRDGV', 'RL_SV')
    ) LOOP
        EXECUTE IMMEDIATE 'DROP ROLE ' || r.role;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN
        NULL; -- Bỏ qua lỗi nếu role không tồn tại
END;
/


CREATE ROLE RL_NVCB; -- 500 người
CREATE ROLE RL_GV; -- 200 người
CREATE ROLE RL_NVPDT; -- 20 người
CREATE ROLE RL_NVPKT; -- 10 người
CREATE ROLE RL_NVTCHC; -- 15 người
CREATE ROLE RL_NVCTSV; -- 10 người
CREATE ROLE RL_TRDGV; -- 15 người
CREATE ROLE RL_SV;

-- Tất cả nhân viên thuộc các vai trò còn lại đều có quyền của vai trò “NVCB”

GRANT RL_NVCB TO RL_TRDGV;
GRANT RL_NVCB TO RL_NVPDT;
GRANT RL_NVCB TO RL_NVPKT;
GRANT RL_NVCB TO RL_NVTCHC;
GRANT RL_NVCB TO RL_NVCTSV;
GRANT RL_NVCB TO RL_GV;


-- Người dùng có VAITRO là “NVCB” có quyền xem dòng dữ liệu của chính mình trong quan hệ NHANVIEN, 
-- có thể chỉnh sửa số điện thoại (ĐT) của chính mình (nếu số điện thoại có thay đổi).

CREATE OR REPLACE VIEW UV_NVCB_CANHAN AS
SELECT *
FROM NHANVIEN
WHERE MANLD = SYS_CONTEXT('USERENV', 'SESSION_USER');

CREATE OR REPLACE TRIGGER TR_NVCB_CANHAN
INSTEAD OF UPDATE ON UV_NVCB_CANHAN
FOR EACH ROW
BEGIN
    IF :OLD.MANLD = :NEW.MANLD AND
       :OLD.HOTEN = :NEW.HOTEN AND
       :OLD.PHAI = :NEW.PHAI AND
       :OLD.NGSINH = :NEW.NGSINH AND
       :OLD.LUONG = :NEW.LUONG AND
       :OLD.PHUCAP = :NEW.PHUCAP AND
       :OLD.VAITRO = :NEW.VAITRO AND
       :OLD.MADV = :NEW.MADV THEN
        
        UPDATE NHANVIEN
        SET DT = :NEW.DT
        WHERE MANLD = :OLD.MANLD;
    ELSE
        RAISE_APPLICATION_ERROR(-20001, 'Chỉ được phép cập nhật số điện thoại!');
    END IF;
END;
/

-- Cấp quyền cho RL_NVCB trên view cá nhân
GRANT SELECT ON UV_NVCB_CANHAN TO RL_NVCB;
GRANT UPDATE ON UV_NVCB_CANHAN TO RL_NVCB;


-- Người dùng có VAITRO là “TRGĐV” có quyền xem các dòng dữ liệu liên quan đến
-- các nhân viên thuộc đơn vị mình làm trưởng, trừ các thuộc tính LUONG và PHUCAP.

CREATE OR REPLACE VIEW UV_TRGDV_NV AS
SELECT MANLD, HOTEN, PHAI, NGSINH, DT, VAITRO, MADV
FROM NHANVIEN
WHERE MADV IN (
    SELECT MADV 
    FROM DONVI 
    WHERE TRGDV = SYS_CONTEXT('USERENV', 'SESSION_USER')
);

GRANT SELECT ON UV_TRGDV_NV TO RL_TRDGV;

-- Người dùng có VAITRO là “NV TCHC” có quyền xem, thêm, cập nhật, xóa trên quan
-- hệ NHANVIEN.

GRANT SELECT, INSERT, UPDATE, DELETE ON NHANVIEN TO RL_NVTCHC;

-- Câu 2: QUAN HE MOMON 
-- Người dùng có vai trò “GV” được quyền xem các dòng phân công giảng dạy liên quan
-- đến chính giảng viên đó

CREATE VIEW UV_GV_CANHAN AS
SELECT * FROM MOMON WHERE MAGV = SYS_CONTEXT('USERENV', 'SESSION_USER');
    GRANT SELECT ON UV_GV_CANHAN TO RL_GV;

-- Người dùng có vai trò “NV PĐT” có quyền xem, thêm, cập nhật, xóa dòng trong bảng
-- MOMON liên quan đến học kỳ hiện tại của năm học đang diễn ra.


CREATE OR REPLACE VIEW UV_NVPDT_MOMON AS 
SELECT *
FROM MOMON
WHERE 
    NAM = CASE 
        WHEN EXTRACT(MONTH FROM SYSDATE) >= 9 THEN EXTRACT(YEAR FROM SYSDATE)
        ELSE EXTRACT(YEAR FROM SYSDATE) - 1
    END
    AND HK = CASE 
        WHEN EXTRACT(MONTH FROM SYSDATE) BETWEEN 9 AND 12 THEN 1
        WHEN EXTRACT(MONTH FROM SYSDATE) BETWEEN 1 AND 4 THEN 2
        WHEN EXTRACT(MONTH FROM SYSDATE) BETWEEN 5 AND 8 THEN 3
    END;

CREATE OR REPLACE TRIGGER TR_NVPDT_MOMON_INSERT 
INSTEAD OF INSERT ON UV_NVPDT_MOMON 
FOR EACH ROW
DECLARE
    v_current_year NUMBER := CASE 
        WHEN EXTRACT(MONTH FROM SYSDATE) >= 9 THEN EXTRACT(YEAR FROM SYSDATE)
        ELSE EXTRACT(YEAR FROM SYSDATE) - 1
    END;
    v_current_hk NUMBER := CASE 
        WHEN EXTRACT(MONTH FROM SYSDATE) BETWEEN 9 AND 12 THEN 1
        WHEN EXTRACT(MONTH FROM SYSDATE) BETWEEN 1 AND 4 THEN 2
        ELSE 3
    END;
BEGIN
    IF :NEW.NAM != v_current_year OR :NEW.HK != v_current_hk THEN
        RAISE_APPLICATION_ERROR(-20002, 'Chỉ được thêm môn mở cho học kỳ và năm học hiện tại.');
    END IF;

    INSERT INTO MOMON (MAMM, MAHP, MAGV, HK, NAM)
    VALUES (:NEW.MAMM, :NEW.MAHP, :NEW.MAGV, :NEW.HK, :NEW.NAM);
END;
/

CREATE OR REPLACE TRIGGER TR_NVPDT_MOMON_UPDATE 
INSTEAD OF UPDATE ON UV_NVPDT_MOMON 
FOR EACH ROW
DECLARE
    v_current_year NUMBER := CASE 
        WHEN EXTRACT(MONTH FROM SYSDATE) >= 9 THEN EXTRACT(YEAR FROM SYSDATE)
        ELSE EXTRACT(YEAR FROM SYSDATE) - 1
    END;
    v_current_hk NUMBER := CASE 
        WHEN EXTRACT(MONTH FROM SYSDATE) BETWEEN 9 AND 12 THEN 1
        WHEN EXTRACT(MONTH FROM SYSDATE) BETWEEN 1 AND 4 THEN 2
        ELSE 3
    END;
BEGIN
    IF :NEW.NAM != v_current_year OR :NEW.HK != v_current_hk THEN
        RAISE_APPLICATION_ERROR(-20003, 'Chỉ được cập nhật môn mở cho học kỳ và năm học hiện tại.');
    END IF;

    UPDATE MOMON
    SET MAHP = :NEW.MAHP,
        MAGV = :NEW.MAGV,
        HK = :NEW.HK,
        NAM = :NEW.NAM
    WHERE MAMM = :OLD.MAMM; 
END;
/

CREATE OR REPLACE TRIGGER TR_NVPDT_MOMON_DELETE 
INSTEAD OF DELETE ON UV_NVPDT_MOMON 
FOR EACH ROW
BEGIN
    DELETE FROM MOMON
    WHERE MAMM = :OLD.MAMM; 
END;
/


GRANT SELECT, INSERT, UPDATE, DELETE ON UV_NVPDT_MOMON TO RL_NVPDT; 

-- Người dùng có vai trò “TRGĐV” có quyền xem các dòng phân công giảng dạy của
-- các giảng viên thuộc đơn vị mình làm trưởng
CREATE OR REPLACE VIEW UV_TRGDV_MOMON AS
SELECT MM.MAMM, MM.MAHP, HP.TENHP, MM.MAGV, NV.HOTEN AS TENGV, MM.HK, MM.NAM
FROM MOMON MM
JOIN NHANVIEN NV ON MM.MAGV = NV.MANLD
JOIN HOCPHAN HP ON MM.MAHP = HP.MAHP
WHERE NV.MADV IN (
    SELECT MADV
    FROM DONVI
    WHERE TRGDV = SYS_CONTEXT('USERENV', 'SESSION_USER')
);
-- Cấp quyền cho TRGDV trên view MOMON
GRANT SELECT ON UV_TRGDV_MOMON TO RL_TRDGV;

-- Sinh viên có quyền xem các dòng dữ liệu trong quan hệ MOMON liên quan các dòng
-- mở các học phần thuộc quyền phụ trách chuyên môn bởi Khoa mà sinh viên đang theo học.
CREATE OR REPLACE VIEW UV_SV_MOMON AS
SELECT MM.MAMM, MM.MAHP, HP.TENHP, NV.HOTEN AS TENGV, MM.HK, MM.NAM, DV.TENDV
FROM MOMON MM
JOIN HOCPHAN HP ON MM.MAHP = HP.MAHP
JOIN NHANVIEN NV ON MM.MAGV = NV.MANLD
JOIN DONVI DV ON HP.MADV = DV.MADV
WHERE HP.MADV = (  
    SELECT KHOA
    FROM SINHVIEN
    WHERE MASV = SYS_CONTEXT('USERENV', 'SESSION_USER')
);

GRANT SELECT ON UV_SV_MOMON TO RL_SV;