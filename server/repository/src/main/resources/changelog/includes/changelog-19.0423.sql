--liquibase formatted SQL

--changeset jnorwood:HB-0
ALTER TABLE `rainbow_table`
DROP FOREIGN KEY `rainbow_table_ibfk_1`;

ALTER TABLE `rainbow_table`
DROP COLUMN `batchExecutionId`;

ALTER TABLE `rainbow_table`
ADD COLUMN `chainsGenerated` BIGINT NOT NULL DEFAULT 0
AFTER `finalChainCount`;

ALTER TABLE `rainbow_table`
ADD COLUMN `status` VARCHAR(24) NOT NULL
AFTER `chainsGenerated`;
