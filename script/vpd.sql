-- Câu 3: QUAN HE SINH VIEN - CHINH SACH VPD 


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

BEGIN
  DBMS_RLS.DROP_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_update_security_policy'
  );
EXCEPTION
  WHEN OTHERS THEN
    NULL;
END;
/

BEGIN
  DBMS_RLS.DROP_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_insert_security_policy'
  );
EXCEPTION
  WHEN OTHERS THEN
    NULL;
END;
/

BEGIN
  DBMS_RLS.DROP_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_delete_security_policy'
  );
EXCEPTION
  WHEN OTHERS THEN
    NULL;
END;
/


CREATE OR REPLACE FUNCTION f_sinhvien_security (
   p_schema VARCHAR2,
   p_object VARCHAR2
) RETURN VARCHAR2 IS
   v_user      VARCHAR2(30) := USER;
   v_prefix    VARCHAR2(10);
   v_predicate VARCHAR2(4000);
   v_madv      VARCHAR2(10);
BEGIN
   -- Xác định prefix role: lấy từ tên user
   IF v_user LIKE 'SV_%' THEN
      RETURN 'MASV = ''' || SUBSTR(v_user, 4) || '''';
   ELSIF v_user LIKE 'GV_%' THEN
      SELECT MADV INTO v_madv FROM NHANVIEN WHERE MANLD = SUBSTR(v_user, 4);
      RETURN 'KHOA = ''' || v_madv || '''';
   ELSIF v_user LIKE 'NVCTSV_%' OR v_user LIKE 'TRGDV_%' OR v_user LIKE 'NVPDT_%' THEN
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
      function_schema  => 'DBA_MANAGER',
      policy_function  => 'f_sinhvien_security',
      statement_types  => 'SELECT'
   );
END;
/

CREATE OR REPLACE FUNCTION f_sinhvien_update_security (
   p_schema VARCHAR2,
   p_object VARCHAR2
) RETURN VARCHAR2 IS
   v_user VARCHAR2(30) := USER;
BEGIN
   IF v_user LIKE 'NVCTSV_%' OR v_user LIKE 'NVPDT_%' THEN
      RETURN '1=1';
   ELSIF v_user LIKE 'SV_%' THEN
      RETURN 'MASV = ''' || SUBSTR(v_user, 4) || '''';
   ELSE
      RETURN '1=0';
   END IF;
END;

/

-- Tạo policy áp dụng cho truy vấn UPDATE
BEGIN
   DBMS_RLS.ADD_POLICY (
      object_schema => 'DBA_MANAGER',
      object_name    => 'SINHVIEN',
      policy_name    => 'sinhvien_update_security_policy',
      function_schema  => 'DBA_MANAGER',
      policy_function  => 'f_sinhvien_update_security',
      statement_types => 'UPDATE'
   );
END;
/
CREATE OR REPLACE TRIGGER trg_sinhvien_update_columns 
BEFORE UPDATE ON SINHVIEN
FOR EACH ROW
DECLARE
   v_user  VARCHAR2(30) := USER;
BEGIN
   IF :NEW.TINHTRANG IS NOT NULL AND :OLD.TINHTRANG IS NULL THEN
      IF NOT v_user LIKE 'NVPDT_%' THEN
         RAISE_APPLICATION_ERROR(-20001, 'Only NVPDT_ users can update TINHTRANG.');
      END IF;
   END IF;

   IF v_user LIKE 'SV_%' THEN
      IF (:NEW.HOTEN != :OLD.HOTEN OR 
          :NEW.PHAI != :OLD.PHAI OR 
          :NEW.NGSINH != :OLD.NGSINH OR 
          :NEW.KHOA != :OLD.KHOA) THEN
         RAISE_APPLICATION_ERROR(-20002, 'Students can only update DCHI and DT.');
      END IF;
   END IF;
END;
/

CREATE OR REPLACE FUNCTION f_sinhvien_insert_security (
   p_schema VARCHAR2,
   p_object VARCHAR2
) RETURN VARCHAR2 IS
   v_user VARCHAR2(30) := USER;
BEGIN
   IF v_user LIKE 'NVCTSV_%' THEN
      RETURN '1=1';
   ELSE
      RETURN '1=0';
   END IF;
END;
/


-- Tạo policy áp dụng cho INSERT
BEGIN
   DBMS_RLS.ADD_POLICY (
      object_schema    => 'DBA_MANAGER',
      object_name      => 'SINHVIEN',
      policy_name      => 'sinhvien_insert_security_policy',
      function_schema  => 'DBA_MANAGER',
      policy_function  => 'f_sinhvien_insert_security',
      statement_types  => 'INSERT',
      update_check => true
   );
END;
/


-- Policy Function for DELETE
CREATE OR REPLACE FUNCTION f_sinhvien_delete_security (
   p_schema VARCHAR2,
   p_object VARCHAR2
) RETURN VARCHAR2 IS
   v_user VARCHAR2(30) := USER;
BEGIN
   IF v_user LIKE 'NVCTSV_%' THEN
      RETURN '1=1';
   ELSE
      RETURN '1=0';
   END IF;
END;
/



-- Tạo policy áp dụng cho DELETE
BEGIN
   DBMS_RLS.ADD_POLICY (
      object_schema    => 'DBA_MANAGER',
      object_name      => 'SINHVIEN',
      policy_name      => 'sinhvien_delete_security_policy',
      function_schema  => 'DBA_MANAGER',
      policy_function  => 'f_sinhvien_delete_security',
      statement_types  => 'DELETE'
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
  
  DBMS_RLS.ENABLE_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_update_security_policy',
    enable        => TRUE
  );

  DBMS_RLS.ENABLE_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_insert_security_policy',
    enable        => TRUE
  );

  DBMS_RLS.ENABLE_POLICY(
    object_schema => 'DBA_MANAGER',
    object_name   => 'SINHVIEN',
    policy_name   => 'sinhvien_delete_security_policy',
    enable        => TRUE
  );
END;
/
-- Câu 4: QUAN HE DANG KY - CHINH SACH VPD