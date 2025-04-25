-- Nếu chưa mở OLS, mở OLS trên CDB bằng các lệnh dưới
-- SELECT name, open_mode FROM v$pdbs;
-- EXEC LBACSYS.CONFIGURE_OLS;
-- EXEC LBACSYS.OLS_ENFORCEMENT.ENABLE_OLS;
-- SHUTDOWN IMMEDIATE;
-- STARTUP;
-- ALTER USER LBACSYS ACCOUNT UNLOCK;
-- ALTER USER LBACSYS IDENTIFIED BY LABCSYS;

-- GRANT EXECUTE ON LBACSYS.SA_COMPONENTS TO DBA_MANAGER WITH GRANT OPTION;
-- GRANT EXECUTE ON LBACSYS.sa_user_admin TO DBA_MANAGER WITH GRANT OPTION;
-- GRANT EXECUTE ON LBACSYS.sa_label_admin TO DBA_MANAGER WITH GRANT OPTION;
-- GRANT EXECUTE ON sa_policy_admin TO DBA_MANAGER WITH GRANT OPTION;
-- GRANT EXECUTE ON char_to_label TO DBA_MANAGER WITH GRANT OPTION;
-- GRANT LBAC_DBA TO DBA_MANAGER;
-- GRANT EXECUTE ON sa_sysdba TO DBA_MANAGER;
-- GRANT EXECUTE ON TO_LBAC_DATA_LABEL TO DBA_MANAGER;

SELECT VALUE FROM v$option WHERE parameter = 'Oracle Label Security';
SELECT status FROM dba_ols_status WHERE name = 'OLS_CONFIGURE_STATUS';

-- 1. Tạo chính sách OLS
BEGIN
  SA_SYSDBA.CREATE_POLICY(
    policy_name   => 'OLS_THONGBAO',
    column_name   => 'OLS_LABEL',
    default_options => 'READ_CONTROL,WRITE_CONTROL,LABEL_DEFAULT'
  );
END;
/

-- 2. Tạo các cấp độ bảo mật (LEVEL)
BEGIN
  SA_COMPONENTS.CREATE_LEVEL(
    policy_name => 'OLS_THONGBAO',
    level_num   => 100,      -- Trưởng đơn vị
    short_name  => 'TRGDV',
    long_name   => 'Truong don vi'
  );

  SA_COMPONENTS.CREATE_LEVEL(
    policy_name => 'OLS_THONGBAO',
    level_num   => 50,       -- Nhân viên
    short_name  => 'NV',
    long_name   => 'Nhan vien'
  );

  SA_COMPONENTS.CREATE_LEVEL(
    policy_name => 'OLS_THONGBAO',
    level_num   => 10,       -- Sinh viên
    short_name  => 'SV',
    long_name   => 'Sinh vien'
  );
END;
/

-- 3. Tạo COMPARTMENTS theo lĩnh vực
BEGIN
  SA_COMPONENTS.CREATE_COMPARTMENT('OLS_THONGBAO', 1, 'TOAN', 'Toan');
  SA_COMPONENTS.CREATE_COMPARTMENT('OLS_THONGBAO', 2, 'LY', 'Ly');
  SA_COMPONENTS.CREATE_COMPARTMENT('OLS_THONGBAO', 3, 'HOA', 'Hoa');
  SA_COMPONENTS.CREATE_COMPARTMENT('OLS_THONGBAO', 4, 'HC', 'Hanh chinh');
END;
/

-- 4. Tạo GROUPS theo cơ sở
BEGIN
  SA_COMPONENTS.CREATE_GROUP('OLS_THONGBAO', 10, 'CS1', 'Co so 1');
  SA_COMPONENTS.CREATE_GROUP('OLS_THONGBAO', 20, 'CS2', 'Co so 2');
END;
/

-- 5. Gán chính sách OLS vào bảng THONGBAO của schema DBA_MANAGER
BEGIN
  SA_POLICY_ADMIN.APPLY_TABLE_POLICY(
    policy_name    => 'OLS_THONGBAO',
    schema_name    => 'DBA_MANAGER',
    table_name     => 'THONGBAO',
    table_options  => 'LABEL_DEFAULT'
  );
END;
/

-- 6. Stored Procedure để tự động gán nhãn theo vai trò người dùng
CREATE OR REPLACE PROCEDURE DBA_MANAGER.USP_AssignOLSLabels AS
BEGIN
  FOR user_rec IN (
    SELECT username FROM dba_users WHERE username IN ('TEST_TRGDV', 'TEST_NVCB', 'TEST_NVTCHC')
  ) LOOP
    IF user_rec.username = 'TEST_TRGDV' THEN
      SA_USER_ADMIN.SET_USER_LABELS(
        policy_name => 'OLS_THONGBAO',
        user_name   => user_rec.username,
        max_read_label => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2',
        max_write_label => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2',
        def_label       => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2',
        row_label       => 'TRGDV:TOAN,HOA,LY,HC:CS1,CS2'
      );
    ELSIF user_rec.username = 'TEST_NVCB' THEN
      SA_USER_ADMIN.SET_USER_LABELS(
        policy_name => 'OLS_THONGBAO',
        user_name   => user_rec.username,
        max_read_label => 'NV:TOAN,LY:CS1',
        max_write_label => 'NV:TOAN,LY:CS1',
        def_label       => 'NV:TOAN,LY:CS1',
        row_label       => 'NV:TOAN,LY:CS1'
      );
    ELSIF user_rec.username = 'TEST_NVTCHC' THEN
      SA_USER_ADMIN.SET_USER_LABELS(
        policy_name => 'OLS_THONGBAO',
        user_name   => user_rec.username,
        max_read_label => 'NV:HC:CS2',
        max_write_label => 'NV:HC:CS2',
        def_label       => 'NV:HC:CS2',
        row_label       => 'NV:HC:CS2'
      );
    END IF;
  END LOOP;
END;
/