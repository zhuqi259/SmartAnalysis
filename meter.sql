DROP DATABASE IF EXISTS `meter`;
GRANT USAGE ON *.* TO meter@localhost;
DROP USER meter@localhost;
DELETE FROM mysql.user WHERE user='meter' and host='localhost';
FLUSH PRIVILEGES;

CREATE DATABASE `meter` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL PRIVILEGES ON meter.* TO meter@localhost IDENTIFIED BY 'meter';
FLUSH PRIVILEGES;

USE `meter`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_meter
-- ----------------------------
DROP TABLE IF EXISTS `t_meter`;
CREATE TABLE `t_meter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `m_time` TIMESTAMP,
  `m_num` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_power
-- ----------------------------
DROP TABLE IF EXISTS `t_power`;
CREATE TABLE `t_power` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `p_time` TIMESTAMP,
  `p_num` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_config
-- ----------------------------
DROP TABLE IF EXISTS `t_config`;
CREATE TABLE `t_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `c_ip` varchar(50) NOT NULL,
  `c_longport` int(11) NOT NULL,
  `c_shortport` int(11) NOT NULL,
  `c_control` varchar(50) NOT NULL,
  `c_meter` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_config
-- ----------------------------
INSERT INTO `t_config` VALUES ('1', '192.168.1.125', '8000', '8001', '022282253522', '467710040000');

commit;
