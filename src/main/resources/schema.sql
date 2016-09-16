CREATE TABLE users (
  username VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (username)
);

CREATE TABLE  user_roles (
  user_role_id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL,
  role VARCHAR(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  CONSTRAINT user_role_username FOREIGN KEY (username) REFERENCES users (username) ON DELETE SET NULL
);

CREATE TABLE tilesets (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL UNIQUE,
  date_created DATE NOT NULL,
  spacing INT,
  offset_x INT,
  offset_y INT,
  tile_width INT NOT NULL,
  tile_height INT NOT NULL,
  num_row INT NOT NULL,
  num_col INT NOT NULL,
  file_name VARCHAR (45) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE projects (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL,
  date_created DATE NOT NULL,
  owner VARCHAR(45),
  PRIMARY KEY (name),
  CONSTRAINT project_user FOREIGN KEY (owner) REFERENCES users (username) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE levels (
  project_id INT NOT NULL,
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR (45) NOT NULL,
  date_created DATE NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT level_project_id FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE
);

