ALTER SESSION SET "_ORACLE_SCRIPT" = true;
ALTER SESSION SET CURRENT_SCHEMA = "DBA_MANAGER";


-- Xóa role nếu có 
BEGIN
    FOR r IN (
        SELECT role FROM dba_roles 
        WHERE role IN ('RL_NVCB', 'RL_GV', 'RL_NVPDT', 'RL_NVPKT', 'RL_NVTCHC', 'RL_NVCTSV', 'RL_TRGDV', 'RL_SV')
    ) LOOP
        EXECUTE IMMEDIATE 'DROP ROLE ' || r.role;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN
        NULL; 
END;
/
-- Yêu cầu 1
-- Câu 1: QUAN HE NHANVIEN
-- Tạo các role cho các vai trò trong hệ thống:

CREATE ROLE RL_NVCB; -- 500 người
CREATE ROLE RL_GV; -- 200 người
CREATE ROLE RL_NVPDT; -- 20 người
CREATE ROLE RL_NVPKT; -- 10 người
CREATE ROLE RL_NVTCHC; -- 15 người
CREATE ROLE RL_NVCTSV; -- 10 người
CREATE ROLE RL_TRGDV; -- 15 người
CREATE ROLE RL_SV;

-- GRANT QUYỀN CHO ROLE
-- TABLE DONVI
GRANT SELECT ON DONVI TO RL_NVTCHC;

---- TABLE SINHVIEN
GRANT SELECT ON SINHVIEN TO RL_SV;
GRANT UPDATE ON SINHVIEN TO RL_SV;
GRANT SELECT ON SINHVIEN TO RL_GV;
GRANT SELECT, UPDATE ON SINHVIEN TO RL_NVPDT;
GRANT INSERT, DELETE, UPDATE ON SINHVIEN TO RL_NVCTSV;
---- TABLE MOMON
GRANT SELECT ON MOMON TO RL_GV;
GRANT SELECT ON MOMON TO RL_SV;
GRANT SELECT ON MOMON TO RL_TRGDV;
GRANT SELECT, INSERT, UPDATE, DELETE ON MOMON TO RL_NVPDT;

---- TABLE NHANVIEN
GRANT SELECT ON NHANVIEN TO RL_NVCB;
GRANT UPDATE ON NHANVIEN TO RL_NVCB;
GRANT SELECT, INSERT, UPDATE, DELETE ON NHANVIEN TO RL_NVTCHC;
GRANT SELECT ON NHANVIEN TO RL_TRGDV;
GRANT SELECT ON NHANVIEN TO RL_NVPDT;


---- TABLE DANGKY
GRANT SELECT ON DANGKY TO RL_GV;
GRANT SELECT, INSERT, UPDATE, DELETE ON DANGKY TO RL_SV;
GRANT SELECT, INSERT, UPDATE, DELETE ON DANGKY TO RL_NVPDT;
GRANT SELECT, UPDATE ON DANGKY TO RL_NVPKT;

---- TABLE HOCPHAN
GRANT SELECT ON HOCPHAN TO RL_SV;
GRANT SELECT ON HOCPHAN TO RL_NVPDT;

-- Người dùng có VAITRO là “NVCB” có quyền xem dòng dữ liệu của chính mình trong quan hệ NHANVIEN, 
-- có thể chỉnh sửa số điện thoại (ĐT) của chính mình (nếu số điện thoại có thay đổi).

CREATE OR REPLACE VIEW UV_NVCB_CANHAN AS
SELECT *
FROM NHANVIEN
WHERE MANLD = REGEXP_SUBSTR(SYS_CONTEXT('USERENV', 'SESSION_USER'), '[^_]+$');


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
GRANT SELECT, UPDATE(DT) ON UV_NVCB_CANHAN TO RL_NVCB;

-- Tất cả nhân viên thuộc các vai trò còn lại đều có quyền của vai trò “NVCB”

GRANT RL_NVCB TO RL_TRGDV;
GRANT RL_NVCB TO RL_NVPDT;
GRANT RL_NVCB TO RL_NVPKT;
GRANT RL_NVCB TO RL_NVTCHC;
GRANT RL_NVCB TO RL_NVCTSV;
GRANT RL_NVCB TO RL_GV;

-- Người dùng có VAITRO là “TRGĐV” có quyền xem các dòng dữ liệu liên quan đến
-- các nhân viên thuộc đơn vị mình làm trưởng, trừ các thuộc tính LUONG và PHUCAP.

CREATE OR REPLACE VIEW UV_TRGDV_NV AS
SELECT MANLD, HOTEN, PHAI, NGSINH, DT, VAITRO, MADV
FROM NHANVIEN
WHERE MADV IN (
    SELECT MADV 
    FROM DONVI 
    WHERE TRGDV = SUBSTR(SYS_CONTEXT('USERENV', 'SESSION_USER'), 7)
);

GRANT SELECT ON UV_TRGDV_NV TO RL_TRGDV;

-- Người dùng có VAITRO là “NV TCHC” có quyền xem, thêm, cập nhật, xóa trên quan
-- hệ NHANVIEN.

GRANT SELECT, INSERT, UPDATE, DELETE ON NHANVIEN TO RL_NVTCHC;

-- Câu 2: QUAN HE MOMON 
-- Người dùng có vai trò “GV” được quyền xem các dòng phân công giảng dạy liên quan
-- đến chính giảng viên đó

CREATE OR REPLACE VIEW UV_GV_CANHAN AS
SELECT * FROM MOMON
WHERE MAGV = SUBSTR(SYS_CONTEXT('USERENV', 'SESSION_USER'), 4);
GRANT SELECT ON UV_GV_CANHAN TO RL_GV;

-- Người dùng có vai trò “NV PĐT” có quyền xem, thêm, cập nhật, xóa dòng trong bảng
-- MOMON liên quan đến học kỳ hiện tại của năm học đang diễn ra.


CREATE OR REPLACE VIEW UV_NVPDT_MOMON AS 
SELECT *
FROM MOMON
WHERE 
    HK = (SELECT MAX(HK) FROM MOMON) AND NAM = (SELECT MAX(NAM) FROM MOMON)
    WITH CHECK OPTION;

GRANT SELECT, INSERT, UPDATE, DELETE ON UV_NVPDT_MOMON TO RL_NVPDT;


-- Người dùng có vai trò “TRGĐV” có quyền xem các dòng phân công giảng dạy của
-- các giảng viên thuộc đơn vị mình làm trưởng
CREATE OR REPLACE VIEW UV_TRGDV_MOMON AS
SELECT MM.*
FROM MOMON MM
JOIN NHANVIEN NV ON MM.MAGV = NV.MANLD
WHERE NV.MADV IN (
    SELECT MADV
    FROM DONVI
    WHERE TRGDV = REGEXP_SUBSTR(SYS_CONTEXT('USERENV', 'SESSION_USER'), '[^_]+$')
);
-- Cấp quyền cho TRGDV trên view MOMON
GRANT SELECT ON UV_TRGDV_MOMON TO RL_TRGDV;

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
    WHERE MASV = SUBSTR(SYS_CONTEXT('USERENV', 'SESSION_USER'), 4)
);

GRANT SELECT ON UV_SV_MOMON TO RL_SV;
COMMIT;