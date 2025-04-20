CREATE OR REPLACE PROCEDURE USP_TAONHANVIEN AS
BEGIN
    FOR nv IN (
        SELECT MANLD, VAITRO
        FROM NHANVIEN
    ) LOOP
        DECLARE
            v_prefix   VARCHAR2(20);
            v_role     VARCHAR2(20);
            v_username VARCHAR2(50);
        BEGIN
            -- Xác định tiền tố theo vai trò
            CASE UPPER(nv.VAITRO)
                WHEN 'GV'      THEN v_prefix := 'GV_';
                WHEN 'NVCB'    THEN v_prefix := 'NVCB_';
                WHEN 'NVPDT'   THEN v_prefix := 'NVPDT_';
                WHEN 'NVPKT'   THEN v_prefix := 'NVPKT_';
                WHEN 'NVTCHC'  THEN v_prefix := 'NVTCHC_';
                WHEN 'NVCTSV'  THEN v_prefix := 'NVCTSV_';
                WHEN 'TRGDV'   THEN v_prefix := 'TRGDV_';
                ELSE v_prefix := 'NV_';
            END CASE;

            v_username := v_prefix || nv.MANLD;

            -- Xóa user nếu đã tồn tại
            BEGIN
                EXECUTE IMMEDIATE 'DROP USER ' || v_username || ' CASCADE';
                DBMS_OUTPUT.PUT_LINE('User ' || v_username || ' đã được xóa.');
            EXCEPTION
                WHEN OTHERS THEN
                    IF SQLCODE = -01918 THEN -- User không tồn tại
                        NULL;
                    ELSE
                        RAISE;
                    END IF;
            END;

            -- Tạo user
            EXECUTE IMMEDIATE 'CREATE USER ' || v_username || ' IDENTIFIED BY "' || v_username || '"';
            DBMS_OUTPUT.PUT_LINE('User ' || v_username || ' được tạo thành công.');

            -- Cấp quyền đăng nhập
            EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO ' || v_username;

            -- Gán role
            CASE UPPER(nv.VAITRO)
                WHEN 'NVCB'     THEN v_role := 'RL_NVCB';
                WHEN 'GV'       THEN v_role := 'RL_GV';
                WHEN 'NVPDT'    THEN v_role := 'RL_NVPDT';
                WHEN 'NVPKT'    THEN v_role := 'RL_NVPKT';
                WHEN 'NVTCHC'   THEN v_role := 'RL_NVTCHC';
                WHEN 'NVCTSV'   THEN v_role := 'RL_NVCTSV';
                WHEN 'TRGDV'    THEN v_role := 'RL_TRGDV';
                ELSE v_role := NULL;
            END CASE;

            IF v_role IS NOT NULL THEN
                EXECUTE IMMEDIATE 'GRANT ' || v_role || ' TO ' || v_username;
                DBMS_OUTPUT.PUT_LINE('Gán role ' || v_role || ' cho ' || v_username);
            ELSE
                DBMS_OUTPUT.PUT_LINE('Không xác định được role cho nhân viên: ' || nv.MANLD);
            END IF;
        END;
    END LOOP;
END;
/

CREATE OR REPLACE PROCEDURE USP_TAOSINHVIEN AS
BEGIN
    FOR sv IN (
        SELECT MASV, PHAI
        FROM SINHVIEN
    ) LOOP
        DECLARE
            v_prefix   VARCHAR2(20);
            v_username VARCHAR2(50);
        BEGIN
            v_prefix := 'SV_';
            v_username := v_prefix || sv.MASV;

            -- Xóa user nếu đã tồn tại
            BEGIN
                EXECUTE IMMEDIATE 'DROP USER ' || v_username || ' CASCADE';
                DBMS_OUTPUT.PUT_LINE('User ' || v_username || ' đã được xóa.');
            EXCEPTION
                WHEN OTHERS THEN
                    IF SQLCODE = -01918 THEN -- User không tồn tại
                        NULL;
                    ELSE
                        RAISE;
                    END IF;
            END;

            -- Tạo user mới
            EXECUTE IMMEDIATE 'CREATE USER ' || v_username || ' IDENTIFIED BY "' || sv.MASV || '"';
            DBMS_OUTPUT.PUT_LINE('User ' || v_username || ' được tạo thành công.');

            EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO ' || v_username;
            EXECUTE IMMEDIATE 'GRANT RL_SV TO ' || v_username;

            DBMS_OUTPUT.PUT_LINE('Gán role RL_SV cho ' || v_username);
        END;
    END LOOP;
END;
/

-- Gọi thủ tục
BEGIN
    USP_TAONHANVIEN;
    USP_TAOSINHVIEN;
END;
/
COMMIT;
