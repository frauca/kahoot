CREATE TABLE IF NOT EXISTS answer (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        answer VARCHAR(50) NOT NULL,
                                        correct BOOLEAN,
                                        question_id INT
);

CREATE TABLE IF NOT EXISTS question (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    question VARCHAR(50) NOT NULL,
                                    quiz_id INT
);

CREATE TABLE IF NOT EXISTS quiz (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS game (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    quiz_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS player (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(50) NOT NULL,
                                    game_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS roll (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     game_id INT NOT NULL,
                                     question_id INT NOT NULL,
                                     start_time TIME,
                                     end_time TIME
);

CREATE TABLE IF NOT EXISTS choice (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      roll_id INT NOT NULL,
                                      answer_id INT NOT NULL,
                                      player_id INT NOT NULL,
                                      choiceTime TIME NOT NULL
);

ALTER TABLE answer
    ADD FOREIGN KEY (question_id)
        REFERENCES question(id)
        ON DELETE CASCADE;

ALTER TABLE question
    ADD FOREIGN KEY (quiz_id)
        REFERENCES quiz(id)
        ON DELETE CASCADE;


ALTER TABLE game
    ADD FOREIGN KEY (quiz_id)
        REFERENCES quiz(id)
        ON DELETE CASCADE;

ALTER TABLE player
    ADD FOREIGN KEY (game_id)
        REFERENCES game(id)
        ON DELETE CASCADE;

ALTER TABLE player
    add column IF NOT EXISTS
        avatar VARCHAR(2) DEFAULT 'NO' NOT NULL;

ALTER TABLE roll
    ADD FOREIGN KEY (game_id)
        REFERENCES game(id)
        ON DELETE CASCADE;

ALTER TABLE roll
    ADD FOREIGN KEY (question_id)
        REFERENCES question(id)
        ON DELETE CASCADE;

ALTER TABLE choice
    ADD FOREIGN KEY (roll_id)
        REFERENCES roll(id)
        ON DELETE CASCADE;

ALTER TABLE choice
    ADD FOREIGN KEY (answer_id)
        REFERENCES answer(id)
        ON DELETE CASCADE;

ALTER TABLE choice
    ADD FOREIGN KEY (player_id)
        REFERENCES player(id)
        ON DELETE CASCADE;

ALTER TABLE game
    add column IF NOT EXISTS
        current_roll INT DEFAULT 2147483647 NOT NULL;
