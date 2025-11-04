-- liquibase formatted sql
-- changeset core:update_db_core_htmlpage-5.1.4-5.1.5.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE htmlpage modify COLUMN id_htmlpage int AUTO_INCREMENT;