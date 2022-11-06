CREATE TABLE `user_info`
(
    `id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`         varchar(255)  NOT NULL COMMENT 'user name',
    `dob`          datetime      NOT NULL COMMENT 'date of birth',
    `address`      varchar(1023) DEFAULT NULL COMMENT 'address',
    `description`  varchar(2047) DEFAULT NULL COMMENT 'description',
    `created_date` datetime      NOT NULL  DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_date` datetime      DEFAULT NULL COMMENT 'updated time',
    `is_deleted`   tinyint(1)    NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
    KEY `username_index` (`name`)
) ENGINE=InnoDB;