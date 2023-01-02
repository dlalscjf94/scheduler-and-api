CREATE TABLE `fileinfo` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `file_name` int,
  `ext_name` varchar(255),
  `file_path` varchar(255),
  `jobdate` datetime
);

CREATE TABLE `filedata` (
  `file_id` int,
  `data_time` varchar(255),
  `subs_num` int,
  `resign_num` int,
  `pay_amount` decimal,
  `used_amount` decimal,
  `sales_amount` decimal
);

ALTER TABLE `filedata` ADD FOREIGN KEY (`file_id`) REFERENCES `fileinfo` (`id`);
