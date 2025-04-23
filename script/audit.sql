-- 1. Kích hoạt việc ghi nhật ký hệ thống
-- Chạy những lệnh này trong CDB với quyền SYSDBA
-- ALTER SYSTEM SET audit_trail=DB, EXTENDED SCOPE=SPFILE;
-- SHUTDOWN IMMEDIATE;
-- STARTUP;

-- 2. Standard Audit
AUDIT SESSION;
AUDIT SELECT, INSERT, UPDATE, DELETE ON DBA_MANAGER.DANGKY BY ACCESS;
AUDIT SELECT, INSERT, UPDATE, DELETE ON DBA_MANAGER.NHANVIEN BY ACCESS;

-- 3.a. Fine-Grained Audit: Cập nhật điểm nhưng không thuộc vai trò "NV PKT"
CREATE OR REPLACE FUNCTION dba_manager.UDF_FGA_DANGKY_DIEM_UPD
RETURN PLS_INTEGER AS
    v_user VARCHAR2(30);
    v_cnt NUMBER;
BEGIN
    v_user := REGEXP_SUBSTR(SYS_CONTEXT('USERENV', 'SESSION_USER'), '[^_]+$');
    SELECT COUNT(*) INTO v_cnt
    FROM DBA_MANAGER.NHANVIEN
    WHERE MANLD = v_user AND VAITRO = 'NV_PKT';
    RETURN CASE WHEN v_cnt = 1 THEN 1 ELSE 0 END;
END;
/

BEGIN
    DBMS_FGA.DROP_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name => 'DANGKY',
        policy_name => 'FGA_DANGKY_DIEM_UPD'
    );
EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema     => 'DBA_MANAGER',
        object_name       => 'DANGKY',
        policy_name       => 'FGA_DANGKY_DIEM_UPD',
        audit_column      => 'DIEMTH,DIEMQT,DIEMCK,DIEMTK',
        audit_condition   => 'dba_manager.UDF_FGA_DANGKY_DIEM_UPD() = 0',
        statement_types   => 'UPDATE'
    );
END;
/


-- 3.b. Fine-Grained Audit: Xem lương/phụ cấp nhưng không phải "NV TCHC"
CREATE OR REPLACE FUNCTION dba_manager.UDF_FGA_NHANVIEN_LUONG_SEL
RETURN PLS_INTEGER AS
    v_user VARCHAR2(30);
    v_cnt NUMBER;
BEGIN
    v_user := REGEXP_SUBSTR(SYS_CONTEXT('USERENV', 'SESSION_USER'), '[^_]+$');
    SELECT COUNT(*) INTO v_cnt
    FROM DBA_MANAGER.NHANVIEN
    WHERE MANLD = v_user AND VAITRO = 'NV_TCHC';
    RETURN CASE WHEN v_cnt = 1 THEN 1 ELSE 0 END;
END;
/

BEGIN
    DBMS_FGA.DROP_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name => 'NHANVIEN',
        policy_name => 'FGA_NHANVIEN_LUONG_SEL'
    );
EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name => 'NHANVIEN',
        policy_name => 'FGA_NHANVIEN_LUONG_SEL',
        audit_column => 'LUONG,PHUCAP',
        audit_condition => 'dba_manager.UDF_FGA_NHANVIEN_LUONG_SEL() = 0',
        statement_types => 'SELECT'
    );
END;
/

-- 3.c. Sinh viên cập nhật dữ liệu người khác hoặc ngoài thời hạn
BEGIN
    DBMS_FGA.DROP_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name => 'DANGKY',
        policy_name => 'FGA_DANGKY_HSKHAC'
    );
EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name => 'DANGKY',
        policy_name => 'FGA_DANGKY_HSKHAC',
        audit_condition => 'REGEXP_SUBSTR(SYS_CONTEXT(''USERENV'', ''SESSION_USER''), ''[^_]+$'') != MASV',
        statement_types => 'INSERT, UPDATE, DELETE'
    );
END;
/

CREATE OR REPLACE FUNCTION dba_manager.UDF_FGA_DANGKY_TRE (
    p_MAMM IN NUMBER
)
RETURN PLS_INTEGER AS
    v_user VARCHAR2(30);
    v_bd_hocky DATE;
BEGIN
    v_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    IF v_user NOT LIKE 'SV%' THEN RETURN 1; END IF;
    SELECT CASE
        WHEN mm.HK = 1 THEN TO_DATE(TO_CHAR(mm.NAM) || '-09-01', 'YYYY-MM-DD') + 14
        WHEN mm.HK = 2 THEN TO_DATE(TO_CHAR(mm.NAM) || '-01-01', 'YYYY-MM-DD') + 14
        WHEN mm.HK = 3 THEN TO_DATE(TO_CHAR(mm.NAM) || '-05-01', 'YYYY-MM-DD') + 14
    END INTO v_bd_hocky
    FROM DBA_MANAGER.MOMON mm
    WHERE mm.MAMM = p_MAMM;
    RETURN CASE WHEN SYSDATE < v_bd_hocky THEN 1 ELSE 0 END;
END;
/

BEGIN
    DBMS_FGA.DROP_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name => 'DANGKY',
        policy_name => 'AUDIT_DANGKY_TRE'
    );
EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name => 'DANGKY',
        policy_name => 'AUDIT_DANGKY_TRE',
        audit_condition => 'dba_manager.UDF_FGA_DANGKY_TRE(MAMM) = 0',
        statement_types => 'INSERT, UPDATE, DELETE'
    );
END;
/

-- Đọc dữ liệu nhật ký
SELECT * FROM SYS.AUD$;
SELECT * FROM DBA_AUDIT_TRAIL;
SELECT * FROM DBA_FGA_AUDIT_TRAIL;

-- Tạo tình huống vi phạm để kiểm tra
-- Ví dụ (tùy chỉnh theo user cụ thể):
-- CONNECT dba_manager/Nhom01
-- UPDATE DBA_MANAGER.DANGKY SET DIEMTH = 10 WHERE MASV = 'SV00000001' AND MAMM = 1;
-- CONNECT SV00000001/SV00000001
-- UPDATE DBA_MANAGER.DANGKY SET DIEMTH = 10 WHERE MASV = 'SV00000002' AND MAMM = 2;
-- Xem lại:
-- SELECT DB_USER, SQL_TEXT, POLICY_NAME FROM DBA_FGA_AUDIT_TRAIL WHERE OBJECT_SCHEMA = 'DBA_MANAGER';
