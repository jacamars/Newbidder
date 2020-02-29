CREATE TABLE IF NOT EXISTS rtb_standards (
  id SERIAL,
  rtbspecification varchar(1024) DEFAULT NULL,
  operator varchar(1024) DEFAULT NULL,
  operand varchar(1024) DEFAULT NULL,
  operand_type varchar(16) DEFAULT NULL,
  operand_ordinal varchar(16) DEFAULT NULL,
  rtb_required int DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  created_at timestamp DEFAULT NULL,
  updated_at timestamp DEFAULT NULL,
  operand_list_id int DEFAULT NULL,
  PRIMARY KEY (id)
);