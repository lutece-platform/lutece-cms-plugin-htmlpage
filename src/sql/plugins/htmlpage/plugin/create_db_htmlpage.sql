-- liquibase formatted sql
-- changeset htmlpage:create_db_htmlpage.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Structure for table htmlpage
--

DROP TABLE IF EXISTS htmlpage;
CREATE TABLE htmlpage (
  id_htmlpage INT AUTO_INCREMENT,
  description varchar(255) DEFAULT '' NOT NULL,
  html_content LONG VARCHAR,
  status INT DEFAULT 0 NOT NULL,
  workgroup_key varchar(50) DEFAULT 'all' NOT NULL,  
  role varchar(50) DEFAULT 'none' NOT NULL,  
  date_start TIMESTAMP NULL,
  date_end TIMESTAMP NULL,
  PRIMARY KEY (id_htmlpage)
);