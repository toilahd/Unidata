-- Câu 3: QUAN HE SINH VIEN - CHINH SACH VPD 
ALTER SESSION SET "_ORACLE_SCRIPT" = true;
ALTER SESSION SET CURRENT_SCHEMA = "DBA_MANAGER";

-- Xóa policy cũ (nếu tồn tại) trước khi tạo mới
BEGIN
  DBMS_RLS.DROP_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_security_policy'
  );
EXCEPTION
  WHEN OTHERS THEN
    NULL;
END;
/

GRANT SELECT, UPDATE(DCHI, DT) ON SINHVIEN TO RL_SV;
GRANT SELECT ON SINHVIEN TO RL_GV;
GRANT SELECT, INSERT, UPDATE, DELETE ON SINHVIEN TO RL_NVCTSV;
GRANT SELECT, UPDATE(TINHTRANG) ON SINHVIEN TO RL_NVPDT;

CREATE OR REPLACE FUNCTION f_sinhvien_security (
   p_schema VARCHAR2,
   p_object VARCHAR2
) RETURN VARCHAR2 IS
   v_user      VARCHAR2(30) := USER;
   v_madv      VARCHAR2(10);
BEGIN
   IF v_user LIKE 'SV_%' THEN
      RETURN 'MASV = ''' || SUBSTR(v_user, 4) || '''';
   ELSIF v_user LIKE 'GV_%' THEN
      SELECT MADV INTO v_madv 
      FROM NHANVIEN 
      WHERE MANLD = SUBSTR(v_user, 4);
      RETURN 'KHOA = ''' || v_madv || '''';
   ELSIF v_user LIKE 'NVCTSV_%' OR v_user LIKE 'NVPDT_%' THEN
      RETURN '1=1';
   ELSE
      RETURN '1=0';
   END IF;
END;
/

-- Tạo policy áp dụng cho truy vấn SELECT
BEGIN
   DBMS_RLS.ADD_POLICY (
      object_schema    => 'DBA_MANAGER',
      object_name      => 'SINHVIEN',
      policy_name      => 'sinhvien_security_policy',
      policy_function  => 'f_sinhvien_security',
      statement_types  => 'SELECT, UPDATE, INSERT, DELETE',
      update_check => true
   );
END;
/

CREATE OR REPLACE TRIGGER trg_sinhvien_update
BEFORE UPDATE ON SINHVIEN
FOR EACH ROW
DECLARE
   v_user  VARCHAR2(30) := USER;
BEGIN
   -- Nếu là sinh viên thì cho phép update (đã được policy giới hạn cột rồi)
   IF v_user LIKE 'SV_%' THEN
      RETURN;
   END IF;

   -- Nếu là NVCTSV, không được sửa TINHTRANG
   IF v_user LIKE 'NVCTSV_%' THEN
      :NEW.TINHTRANG := :OLD.TINHTRANG;
   END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_sinhvien_insert
BEFORE INSERT ON SINHVIEN
FOR EACH ROW
DECLARE
   v_user VARCHAR2(30) := USER;
BEGIN
   -- Nếu là NVCTSV, không được nhập giá trị TINHTRANG
   IF v_user LIKE 'NVCTSV_%' THEN
      IF :NEW.TINHTRANG IS NOT NULL THEN
         RAISE_APPLICATION_ERROR(-20010, 'Chỉ NV PĐT mới được cập nhật TINHTRANG sau khi tạo sinh viên.');
      END IF;
   END IF;
END;
/


-- Câu 4: QUAN HE DANG KY - CHINH SACH VPD

-- SV được cấp đủ quyền trên DANGKY
GRANT SELECT, INSERT, UPDATE, DELETE ON DANGKY TO RL_SV;
-- NVPKT chỉ được xem và cập nhật điểm số
GRANT SELECT, UPDATE (DIEMTH, DIEMQT, DIEMCK, DIEMTK) ON DANGKY TO RL_NVPKT;
-- GV chỉ cần SELECT
GRANT SELECT ON DANGKY TO RL_GV;
-- NVPDT được toàn quyền như sinh viên
GRANT SELECT, INSERT, UPDATE, DELETE ON DANGKY TO RL_NVPDT;


-- Xóa policy cũ (nếu tồn tại) trước khi tạo mới
BEGIN
    DBMS_RLS.DROP_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name   => 'DANGKY',
        policy_name   => 'POLICY_DANGKY_SELECT'
    );
    
    DBMS_RLS.DROP_POLICY(
        object_schema => 'DBA_MANAGER',
        object_name   => 'DANGKY',
        policy_name   => 'POLICY_DANGKY_IUD'
    );
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Có thể policy không tồn tại hoặc lỗi khác: ' || SQLERRM);
END;
/


CREATE OR REPLACE FUNCTION f_dangky_policy_sel (
    p_schema VARCHAR2,
    p_obj    VARCHAR2
)
RETURN VARCHAR2 AS
    v_user   VARCHAR2(30) := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_role   NVARCHAR2(20);
BEGIN
    -- Sinh viên
    IF v_user LIKE 'SV_%' THEN
        RETURN 'MASV = ''' || SUBSTR(v_user, 4) || '''';

    -- Giảng viên
    ELSIF v_user LIKE 'GV_%' THEN
        RETURN 'MAMM IN (SELECT MAMM FROM MOMON WHERE MAGV = ''' || SUBSTR(v_user, 4) || ''')';

    -- Nhân viên
    ELSIF v_user LIKE 'NV%' OR v_user LIKE 'NVPDT_%' OR v_user LIKE 'NVPKT_%' THEN
        BEGIN
            SELECT VAITRO INTO v_role FROM NHANVIEN WHERE MANLD = SUBSTR(v_user, INSTR(v_user, '_') + 1);
            
            IF v_role IN ('NV_PDT', 'NV PKT') THEN
                RETURN '1=1'; -- Full access
            END IF;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RETURN '1=0';
        END;
    END IF;

    RETURN '1=0'; -- Mặc định chặn
END;
/


CREATE OR REPLACE FUNCTION f_dangky_policy_iud (
    p_schema VARCHAR2,
    p_obj    VARCHAR2
)
RETURN VARCHAR2 AS
    v_user    VARCHAR2(30) := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_role    VARCHAR2(20);
    v_hk      NUMBER;
    v_nam     NUMBER;
    v_start   DATE;
    v_end     DATE;
    v_month   VARCHAR2(2);
    v_masv    VARCHAR2(10);
    v_manv    VARCHAR2(10);
BEGIN
    -- Nếu là sinh viên
    IF v_user LIKE 'SV_%' THEN
        v_masv := SUBSTR(v_user, 4);

        SELECT HK, NAM INTO v_hk, v_nam
        FROM MOMON
        WHERE MAMM IN (SELECT MAMM FROM DANGKY WHERE MASV = v_masv)
        FETCH FIRST 1 ROWS ONLY;

        CASE v_hk
            WHEN 1 THEN v_month := '09';
            WHEN 2 THEN v_month := '01';
            WHEN 3 THEN v_month := '05';
            ELSE v_month := '01';
        END CASE;

        v_start := TO_DATE('01-' || v_month || '-' || v_nam, 'DD-MM-YYYY');
        v_end := v_start + 14;

        RETURN 'MASV = ''' || v_masv || ''' AND SYSDATE <= TO_DATE(''' || TO_CHAR(v_end, 'DD-MM-YYYY') || ''', ''DD-MM-YYYY'')';

    -- Nếu là nhân viên NVPDT
    ELSIF v_user LIKE 'NVPDT_%' THEN
        v_manv := SUBSTR(v_user, 8);

        SELECT HK, NAM INTO v_hk, v_nam
        FROM MOMON
        WHERE ROWNUM = 1;

        CASE v_hk
            WHEN 1 THEN v_month := '09';
            WHEN 2 THEN v_month := '01';
            WHEN 3 THEN v_month := '05';
            ELSE v_month := '01';
        END CASE;

        v_start := TO_DATE('01-' || v_month || '-' || v_nam, 'DD-MM-YYYY');
        v_end := v_start + 14;

        RETURN 'SYSDATE <= TO_DATE(''' || TO_CHAR(v_end, 'DD-MM-YYYY') || ''', ''DD-MM-YYYY'')';

    END IF;

    RETURN '1=0'; -- Mặc định chặn
END;

/ 
BEGIN
    -- SELECT Policy
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'DBA_MANAGER',
        object_name     => 'DANGKY',
        policy_name     => 'POLICY_DANGKY_SELECT',
        policy_function => 'f_dangky_policy_sel',
        statement_types => 'SELECT',
        update_check    => TRUE
    );

    -- INSERT, UPDATE, DELETE Policy
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'DBA_MANAGER',
        object_name     => 'DANGKY',
        policy_name     => 'POLICY_DANGKY_IUD',
        policy_function => 'f_dangky_policy_iud',
        statement_types => 'INSERT, UPDATE, DELETE',
        update_check    => TRUE
    );
END;
/

-- enable (TRUE) / disable (FALSE)
BEGIN
  DBMS_RLS.ENABLE_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_security_policy',
    enable        => TRUE
  );
END;
/
-- Câu 4: QUAN HE DANG KY - CHINH SACH VPD
