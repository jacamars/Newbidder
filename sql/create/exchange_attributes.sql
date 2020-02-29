CREATE TABLE IF NOT EXISTS exchange_attributes (
  id SERIAL,
  banner_id int DEFAULT NULL,
  banner_video_id int DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  value varchar(255) DEFAULT NULL,
  created_at timestamp DEFAULT NULL,
  updated_at timestamp DEFAULT NULL,
  exchange varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);
