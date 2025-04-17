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
   v_user      VARCHAR2(30);
   v_role      VARCHAR2(30);
   v_madv      VARCHAR2(10);
   v_predicate VARCHAR2(4000);
BEGIN

   SELECT USER INTO v_user FROM DUAL;

   BEGIN
      SELECT 'SV' INTO v_role FROM SINHVIEN WHERE MASV = v_user;
   EXCEPTION
      WHEN NO_DATA_FOUND THEN

         BEGIN
            SELECT VAITRO, MADV INTO v_role, v_madv
            FROM NHANVIEN 
            WHERE MANLD = v_user;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
               v_role := NULL;
         END;
   END;

   CASE v_role
      WHEN 'NV PCTSV' THEN 
         v_predicate := '1=1';  
      WHEN 'NV PĐT' THEN 
         v_predicate := '1=1'; 
      WHEN 'GV' THEN
         v_predicate := 'KHOA = ''' || v_madv || '''';
      WHEN 'SV' THEN 
         v_predicate := 'MASV = ''' || v_user || '''';
      ELSE 
         v_predicate := '1=0';  
   END CASE;

   RETURN v_predicate;
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
   v_user  VARCHAR2(30);
   v_role  VARCHAR2(30);
BEGIN
   SELECT USER INTO v_user FROM DUAL;

   -- Determine role
   BEGIN
      SELECT 'SV' INTO v_role FROM SINHVIEN WHERE MASV = v_user;
   EXCEPTION
      WHEN NO_DATA_FOUND THEN
         BEGIN
            SELECT VAITRO INTO v_role FROM NHANVIEN WHERE MANLD = v_user;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN v_role := NULL;
         END;
   END;


   IF v_role IN ('NV PCTSV', 'NV PĐT') THEN
      RETURN '1=1';  
   ELSIF v_role = 'SV' THEN
      RETURN 'MASV = ''' || v_user || '''';  
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
   v_user  VARCHAR2(30);
   v_role  VARCHAR2(30);
BEGIN
   SELECT USER INTO v_user FROM DUAL;

   BEGIN
      SELECT 'SV' INTO v_role FROM SINHVIEN WHERE MASV = v_user;
   EXCEPTION
      WHEN NO_DATA_FOUND THEN
         BEGIN
            SELECT VAITRO INTO v_role FROM NHANVIEN WHERE MANLD = v_user;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN v_role := NULL;
         END;
   END;

   IF :NEW.TINHTRANG IS NOT NULL AND :OLD.TINHTRANG IS NULL THEN
      IF v_role != 'NV PĐT' THEN
         RAISE_APPLICATION_ERROR(-20001, 'Only NV PĐT can update TINHTRANG.');
      END IF;
   END IF;

   IF v_role = 'SV' THEN
      IF (:NEW.HOTEN != :OLD.HOTEN OR 
          :NEW.PHAI != :OLD.PHAI OR 
          :NEW.NGSINH != :OLD.NGSINH OR 
          :NEW.KHOA != :OLD.KHOA) THEN
         RAISE_APPLICATION_ERROR(-20002, 'Students can only update DCHI and DT.');
      END IF;
   END IF;
END;

SELECT * FROM v$version;
/
-- Policy Function for INSERT
CREATE OR REPLACE FUNCTION f_sinhvien_insert_security (
   p_schema VARCHAR2,
   p_object VARCHAR2
) RETURN VARCHAR2 IS
   v_user VARCHAR2(30);
   v_role VARCHAR2(30);
BEGIN
   SELECT USER INTO v_user FROM DUAL;

   -- Determine role
   BEGIN
      SELECT VAITRO INTO v_role FROM NHANVIEN WHERE MANLD = v_user;
   EXCEPTION
      WHEN NO_DATA_FOUND THEN
         v_role := NULL;
   END;

   IF v_role = 'NV PCTSV' THEN
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
   v_user VARCHAR2(30);
   v_role VARCHAR2(30);
BEGIN
   SELECT USER INTO v_user FROM DUAL;

   -- Determine role
   BEGIN
      SELECT VAITRO INTO v_role FROM NHANVIEN WHERE MANLD = v_user;
   EXCEPTION
      WHEN NO_DATA_FOUND THEN
         v_role := NULL;
   END;

   IF v_role = 'NV PCTSV' THEN
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
-- Câu 4: QUAN HE DANG KY - CHINH SACH VPD