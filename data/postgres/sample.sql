insert into banners(interval_start,
        interval_end,
        width,
        height,
        bid_ecpm,
        created_at,
        updated_at,
        htmltemplate,
        contenttype) values(NOW(),
        NOW() + '3 days',
        3.5,
        320,
        50,
        NOW(),
        NOW(),
        '<img src=''http://localhost/www.test.jpeg''>',
        'image/jpeg');
  
insert into banner_videos (interval_start,
        interval_end,
        vast_video_width,
        vast_video_height,
        vast_video_linerarity,
        vast_video_duration,
        vast_video_outgoing_file,
        bitrate,
        mime_type,
        created_at,
        updated_at) VALUES(NOW(),
        NOW(),
        600,
        400,
        2,
        120,
        'your-vast-tag-goes-here',
        64000,
        'video/mpeg2',
        NOW(),
        NOW());
        
insert into targets(
        country,
        created_at,
        updated_at,
        name) values('USA',
        NOW(),
        NOW(),
        'Sample target  for USA');
        
insert  into campaigns (
        activate_time,
        expire_time,
        regions,
        target_id,
        status,
        created_at,
        updated_at) values(NOW(),
        NOW()  + '3 days',
        'USA',
        123,
        'runnable',
        NOW(),
        NOW());
        
insert into rtb_standards (created_at, 
        updated_at,
        rtbspecification,
        operator,
        operand,
        operand_type,
        operand_ordinal,
        rtb_required,
        name,
        description) VALUES(now(),
        now(),
        'requests.device.geo',
        'INRANGE',
        'ZIPCODES 90501,90502,90503,90504,90505,10000',
        'S',
        'L',
        1,
        'LAT/LON',
        'Demonstates 5 zipcodes and a range of 10km.');


insert into banners_rtb_standards set(banner_id, rtb_standard_id) 
		VALUES(1,7);   

insert into rtb_standards (created_at,
        updated_at,
        rtbspecification,
        operator,
        operand,
        operand_type,
        operand_ordinal,
        rtb_required,
        name,
        description) VALUES(now(),
        now(),
        'requests.device.geo.region',
        'MEMBER',
        'NY,CA,NV',
        'S',
        'L',
        1,
        '3 states',
        'Demonstrates a constraint of 3 states');
        
 insert into banner_videos_rtb_standards (banner_video_id,rtb_standard_id) values(30,2);
           
        
        