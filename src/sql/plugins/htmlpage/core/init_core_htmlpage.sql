-- liquibase formatted sql
-- changeset htmlpage:init_core_htmlpage.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES
('HTMLPAGE_MANAGEMENT','htmlpage.adminFeature.htmlpage_management.name',3,'jsp/admin/plugins/htmlpage/ManageHtmlPage.jsp','htmlpage.adminFeature.htmlpage_management.description',0,'htmlpage','APPLICATIONS',NULL,NULL);


--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('HTMLPAGE_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('HTMLPAGE_MANAGEMENT',2);
INSERT INTO core_user_right (id_right,id_user) VALUES ('HTMLPAGE_MANAGEMENT',3);

--
-- Cache status for table core_datastore
--
-- The cache status lives in core_datastore, read by CacheService.getStatus( ) under the
-- key core.cache.status.<CacheableService.getName( )>.enabled, defaulting to '0' when the
-- key is missing. It must therefore be declared here.
-- It used to be carried by webapp/WEB-INF/conf/caches.dat, which is only a seed file for
-- the datastore, and which collided with the file of the same path shipped by lutece-core.
--
INSERT INTO core_datastore VALUES ( 'core.cache.status.publicHtmlPageCacheService.enabled', '1' );
