--liquibase formatted SQL

--changeset jnorwood:HB-0
ALTER TABLE `rainbow_table`
ADD COLUMN `generateStarted` DATETIME
AFTER `status`;

ALTER TABLE `rainbow_table`
ADD COLUMN `generateCompleted` DATETIME
AFTER `generateStarted`;
