INSERT INTO users VALUES
  ('admin', 'admin', 1);

INSERT INTO user_roles VALUES
  (default, 'admin', 'ROLE_ADMIN');

INSERT INTO tilesets VALUES
  (default, 'mario', CURRENT_DATE(), 2, 0, 0, 32, 32, 6, 12, 'mario.png');

INSERT INTO projects VALUES
  (default, 'demo-project', CURRENT_DATE(), 'admin');

INSERT INTO levels VALUES
  (1, default, 'demo-level', CURRENT_DATE());
