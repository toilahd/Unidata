-- Oracle 21c, Unified Auditing mặc định được bật.
-- Để kiểm tra Unified Auditing có được bật hay không, sử dụng câu lệnh sau:
SELECT VALUE FROM V$OPTION WHERE PARAMETER = 'Unified Auditing';
-- Nếu giá trị là 'TRUE', Unified Auditing đã được bật.


-- 2. 
-- 3. 

-- a. Hành vi cập nhật quan hệ ĐANGKY tại các trường liên quan đến điểm số nhưng người đó không thuộc vai trò “NV PKT”.

BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'DBA_MANAGER',
    object_name     => 'DANGKY',
    policy_name     => 'AUD_UPDATE_DIEM_NOT_NVPKT',
    audit_condition => 'SYS_CONTEXT(''USERENV'', ''SESSION_USER'') NOT IN (SELECT username FROM DBA_MANAGER.ACCOUNT WHERE VAITRO = ''NV PKT'')',
    audit_column    => 'DIEMTH, DIEMQT, DIEMCK, DIEMTK',
    statement_types => 'UPDATE',
    audit_trail     => DBMS_FGA.DB + DBMS_FGA.EXTENDED
  );
END;
/

BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema   => 'DBA_MANAGER',
    object_name     => 'NHANVIEN',
    policy_name     => 'AUD_LUONG_ACCESS',
    audit_condition => 'SYS_CONTEXT(''USERENV'', ''SESSION_USER'') NOT IN (SELECT username FROM DBA_MANAGER.ACCOUNT WHERE VAITRO = ''NV TCHC'')',
    audit_column    => 'LUONG, PHUCAP',
    statement_types => 'SELECT, UPDATE',
    audit_trail     => DBMS_FGA.DB + DBMS_FGA.EXTENDED
  );
END;
/

