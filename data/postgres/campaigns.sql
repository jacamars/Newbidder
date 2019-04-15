CREATE TABLE IF NOT EXISTS campaigns (
  id SERIAL,
  activate_time timestamp DEFAULT NULL,
  expire_time timestamp DEFAULT NULL,
  cost decimal(15,6) DEFAULT NULL,
  ad_domain varchar(1024)  DEFAULT NULL,
  clicks int DEFAULT NULL,
  pixels int DEFAULT NULL,
  wins int DEFAULT NULL,
  bids int DEFAULT NULL,
  name varchar(1024) DEFAULT NULL,
  status varchar(1024) DEFAULT NULL,
  conversion_type varchar(1024) DEFAULT NULL,
  budget_limit_daily decimal(15,6) DEFAULT NULL,
  budget_limit_hourly decimal(15,6) DEFAULT NULL,
  total_budget decimal(15,6) DEFAULT NULL,
  bid decimal(15,6) DEFAULT NULL,
  shard text,
  forensiq text,
  daily_cost decimal(15,6) DEFAULT NULL,
  updated_at timestamp DEFAULT NULL,
  deleted_at timestamp DEFAULT NULL,
  created_at timestamp DEFAULT NULL,
  hourly_cost decimal(15,6) DEFAULT NULL,
  exchanges varchar(255) DEFAULT NULL,
  regions varchar(255) DEFAULT NULL,
  target_id int DEFAULT NULL,
  PRIMARY KEY (id)
);
