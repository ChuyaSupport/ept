CREATE TABLE `business` (
  `b_id` char(11) NOT NULL,
  `shop_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `b_account` char(11) NOT NULL,
  `b_passwd` varchar(50) NOT NULL,
  `shop_addr` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `b_tel` char(11) NOT NULL,
  `dev_price` varchar(4) DEFAULT NULL,
  `dev_distance` varchar(10) DEFAULT NULL,
  `b_monthly_sales` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`b_id`),
  UNIQUE KEY `b_account` (`b_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `com_img` (
  `Img_id` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `com_img` varchar(50) DEFAULT NULL,
  `com_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Img_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `commodity` (
  `com_id` char(20) NOT NULL,
  `b_id` char(11) DEFAULT NULL,
  `com_name` varchar(20) NOT NULL,
  `com_des` varchar(50) DEFAULT NULL,
  `com_type` char(3) DEFAULT NULL,
  `com_price` float NOT NULL,
  `com_monthly_sales` varchar(10) DEFAULT NULL,
  `comments` float DEFAULT NULL,
  `ranking` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`com_id`),
  KEY `b_id` (`b_id`),
  CONSTRAINT `commodity_ibfk_1` FOREIGN KEY (`b_id`) REFERENCES `business` (`b_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `customer` (
  `c_id` char(11) NOT NULL,
  `c_usrname` varchar(20) NOT NULL,
  `c_account` char(11) NOT NULL,
  `c_passwd` varchar(50) NOT NULL,
  `c_tel` char(11) NOT NULL,
  `c_addr` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `c_status` char(1) DEFAULT '1',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `c_account` (`c_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `follow` (
  `f_id` char(11) NOT NULL,
  `c_id` char(11) DEFAULT NULL,
  `b_id` char(11) DEFAULT NULL,
  PRIMARY KEY (`f_id`),
  KEY `c_id` (`c_id`),
  KEY `b_id` (`b_id`),
  CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`c_id`) REFERENCES `customer` (`c_id`),
  CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`b_id`) REFERENCES `business` (`b_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `manager` (
  `m_id` char(11) NOT NULL,
  `b_id` char(11) DEFAULT NULL,
  `c_id` char(11) DEFAULT NULL,
  `m_time` timestamp NULL DEFAULT NULL,
  `opt_type` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`m_id`),
  KEY `b_id` (`b_id`),
  KEY `c_id` (`c_id`),
  CONSTRAINT `manager_ibfk_1` FOREIGN KEY (`b_id`) REFERENCES `business` (`b_id`),
  CONSTRAINT `manager_ibfk_2` FOREIGN KEY (`c_id`) REFERENCES `customer` (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orders` (
  `o_id` char(20) NOT NULL,
  `c_id` char(11) NOT NULL,
  `b_id` char(11) NOT NULL,
  `com_id` char(20) NOT NULL,
  `com_number` smallint NOT NULL,
  `o_status` char(1) DEFAULT '1',
  `o_time` timestamp NULL DEFAULT NULL,
  `o_notes` varchar(50) DEFAULT NULL,
  `c_score` char(1) DEFAULT '5',
  PRIMARY KEY (`o_id`),
  KEY `c_id` (`c_id`),
  KEY `b_id` (`b_id`),
  KEY `com_id` (`com_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`c_id`) REFERENCES `customer` (`c_id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`b_id`) REFERENCES `business` (`b_id`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`com_id`) REFERENCES `commodity` (`com_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sys_admin` (
  `m_id` char(11) NOT NULL,
  `m_name` varchar(10) NOT NULL,
  `m_account` char(11) NOT NULL,
  `m_passwd` varchar(50) NOT NULL,
  `m_tel` char(11) NOT NULL,
  `m_permission` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`m_id`),
  UNIQUE KEY `m_account` (`m_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `syslog` (
  `latest_bid` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '10000000000',
  `latest_cid` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '10000000000',
  `latest_mid` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '10000000000',
  `latest_comid` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '10000000000000000000',
  `latest_imgid` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '10000000000000000000',
  `latest_oid` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '10000000000000000000'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `grouppool` (
  `g_id` char(20) NOT NULL,
  `com_id` char(20) NOT NULL,
  `c_id` char(11) NOT NULL,
  `start_time` timestamp NOT NULL,
  `ok_num` char(2) NOT NULL,
  `present` char(2) DEFAULT '0',
  `status` char(1) DEFAULT '0',
  PRIMARY KEY (`g_id`),
  KEY `com_id` (`com_id`),
  KEY `c_id` (`c_id`),
  CONSTRAINT `grouppool_ibfk_1` FOREIGN KEY (`com_id`) REFERENCES `commodity` (`com_id`),
  CONSTRAINT `grouppool_ibfk_2` FOREIGN KEY (`c_id`) REFERENCES `customer` (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `group` (
  `g_id` char(20) NOT NULL,
  `c_id` char(11) NOT NULL,
  PRIMARY KEY (`g_id`,`c_id`),
  KEY `c_id` (`c_id`),
  CONSTRAINT `group_ibfk_1` FOREIGN KEY (`g_id`) REFERENCES `grouppool` (`g_id`),
  CONSTRAINT `group_ibfk_2` FOREIGN KEY (`c_id`) REFERENCES `customer` (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table groupmember(
	g_id char(20),
	c_id char(11),
	status char(1) default'0',
	primary key(g_id, c_id, status),
	foreign key(g_id) references grouppool(g_id),
	foreign key(c_id) references customer(c_id)
);

create view v_checkjoinstatus
as(
	select groupmember.c_id, grouppool.com_id, groupmember.status from groupmember 
	left join grouppool on groupmember.g_id = grouppool.g_id 
);

create view v_checkjoinnum
as(
	select groupmember.g_id, COUNT(*) as 'present', grouppool.ok_num from groupmember, grouppool 
	where groupmember.g_id = grouppool.g_id and grouppool.status <> '1' GROUP BY groupmember.g_id
);


create view v_freeorderview
as(
select grouppool.g_id, commodity.group_order_num, commodity.free_order_num from grouppool left join commodity on
grouppool.com_id = commodity.com_id where grouppool.`status` = '0');
