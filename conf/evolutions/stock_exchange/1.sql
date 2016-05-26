# Quotation schema

# --- !Ups
CREATE TABLE IF NOT EXISTS `quotation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) NOT NULL,
  `opening` float NOT NULL,
  `max` float NOT NULL,
  `min` float NOT NULL,
  `closing` float NOT NULL,
  `change_percentage` float NOT NULL,
  `volume` int(11) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`)
)

# --- !Downs
drop table `quotation`