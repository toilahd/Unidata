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

