INSERT INTO users VALUES
  ('admin', 'admin', 1),
  ('anonymousUser', 'anonymousUser', 1);

INSERT INTO user_roles VALUES
  (default, 'admin', 'ROLE_ADMIN'),
  (default, 'anonymousUser', 'ROLE_ADMIN');

INSERT INTO tilesets VALUES
  (default, 'mario', CURRENT_DATE(), 2, 0, 0, 32, 32, 6, 12, 'mario.png');
