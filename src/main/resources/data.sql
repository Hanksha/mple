INSERT INTO users VALUES
  ('admin', 'admin', 1),
  ('user', 'user', 1);

INSERT INTO user_roles VALUES
  (default, 'admin', 'ROLE_ADMIN'),
  (default, 'admin', 'ROLE_USER'),
  (default, 'user', 'ROLE_USER');

INSERT INTO tilesets VALUES
  (default, 'mario', CURRENT_DATE(), 2, 0, 0, 32, 32, 6, 12);
