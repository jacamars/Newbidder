CREATE TABLE IF NOT EXISTS users (
  id serial,
  customer_id text NOT NULL,
  sub_id text DEFAULT NULL,
  username text  NOT NULL,
  password text  NOT NULL,
  PRIMARY KEY (id)
);
