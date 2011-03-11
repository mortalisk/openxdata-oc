${mysql.selected.comment} SET password = password('${mysql.root.password.dy}');

DROP DATABASE IF EXISTS ${mysql.openxdata.dbname};
CREATE DATABASE IF NOT EXISTS ${mysql.openxdata.dbname};

USE ${mysql.openxdata.dbname};

DELIMITER //
CREATE PROCEDURE user_manage()
BEGIN

IF NOT EXISTS ( SELECT 1 FROM mysql.user where user='${mysql.openxdata.username}' AND host='localhost' )
THEN CREATE USER '${mysql.openxdata.username}'@'localhost' IDENTIFIED BY '${mysql.openxdata.password.dy}';
END IF;

IF NOT EXISTS ( SELECT 1 FROM mysql.user where user='${mysql.openxdata.username}' AND host='%' )
THEN CREATE USER '${mysql.openxdata.username}'@'%' IDENTIFIED BY '${mysql.openxdata.password.dy}';
END IF;

GRANT ALL ON ${mysql.openxdata.dbname}.* TO '${mysql.openxdata.username}'@'localhost';

GRANT ALL ON ${mysql.openxdata.dbname}.* TO '${mysql.openxdata.username}'@'%';

END//
DELIMITER ;

CALL user_manage();

drop procedure user_manage;