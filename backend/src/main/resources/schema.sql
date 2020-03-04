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


ALTER TABLE answer
    ADD FOREIGN KEY (question_id)
        REFERENCES question(id)
        ON DELETE CASCADE;

ALTER TABLE question
    ADD FOREIGN KEY (quiz_id)
        REFERENCES quiz(id)
        ON DELETE CASCADE;;
