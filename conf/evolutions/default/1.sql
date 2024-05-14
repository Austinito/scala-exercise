-- !Ups
CREATE TABLE LOGIN_PROMPT (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  CAPTION varchar(255) NOT NULL,
  IMAGE_URL varchar(255) NOT NULL
);

INSERT INTO LOGIN_PROMPT (CAPTION, IMAGE_URL) VALUES
  ('Play Merion!', 'image_merion.jpg'),
  ('Enter the Virtual US Open!', 'image_vuso.jpg'),
  ('Check out the proshop!', 'image_proshop.jpg');

--!Downs
DROP TABLE LoginPrompt;