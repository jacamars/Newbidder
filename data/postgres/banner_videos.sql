CREATE TABLE IF NOT EXISTS banner_videos (
  id SERIAL,
  type text NOT NULL,
  campaign_id  int DEFAULT NULL,
  interval_start timestamp DEFAULT NULL,
  interval_end timestamp DEFAULT NULL,
  total_budget decimal(15,6) DEFAULT NULL,
  vast_video_width int DEFAULT NULL,
  vast_video_height int DEFAULT NULL,
  vast_video_linearity int DEFAULT NULL,
  bid_ecpm decimal DEFAULT NULL,
  cur text DEFAULT NULL,
  vast_video_linerarity int DEFAULT NULL,
  vast_video_duration int DEFAULT NULL,
  vast_video_type text,
  vast_video_protocol int DEFAULT NULL,
  vast_video_outgoing_file text,
  bids int DEFAULT NULL,
  clicks int DEFAULT NULL,
  pixels int DEFAULT NULL,
  wins int DEFAULT NULL,
  total_cost decimal DEFAULT '0.000000',
  daily_cost decimal DEFAULT NULL,
  daily_budget decimal DEFAULT NULL,
  frequency_spec text,
  frequency_expire int DEFAULT NULL,
  frequency_count int DEFAULT NULL,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL,
  hourly_budget decimal DEFAULT NULL,
  name varchar(256) DEFAULT NULL,
  target_id int DEFAULT NULL,
  hourly_cost decimal DEFAULT NULL,
  vast_video_bitrate int DEFAULT NULL,
  mime_type varchar(255)  DEFAULT NULL,
  deals varchar(255) DEFAULT NULL,
  width_range varchar(255) DEFAULT NULL,
  height_range varchar(255) DEFAULT NULL,
  width_height_list varchar(255) DEFAULT NULL,
  rules integer[] DEFAULT NULL,
  interstitial int DEFAULT NULL,
  htmltemplate text,
  PRIMARY KEY (id)
);
