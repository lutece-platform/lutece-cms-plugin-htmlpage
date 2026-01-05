-- liquibase formatted sql
-- changeset core:update_db_core_htmlpage-5.1.4-5.1.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url='ti ti-file-code' WHERE id_right='HTMLPAGE_MANAGEMENT';