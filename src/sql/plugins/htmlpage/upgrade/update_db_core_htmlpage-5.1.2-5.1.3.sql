--
-- Update Structure for table htmlpage
--

ALTER TABLE htmlpage
ADD COLUMN date_start TIMESTAMP NULL DEFAULT NULL;

ALTER TABLE htmlpage
ADD COLUMN date_end TIMESTAMP NULL DEFAULT NULL;

update htmlpage set status = 11 where status = 0;
update htmlpage set status = 12 where status = 1;
update htmlpage set status = 1 where status = 11;
update htmlpage set status = 0 where status = 12;