-- !Ups
-- Update the Login Prompt table to add quiteTimeSec column
ALTER TABLE LOGIN_PROMPT ADD COLUMN QUIET_PERIOD_SEC INT NULL;

-- Add new table UserPromptView
CREATE TABLE USER_PROMPT_VIEW (
    USER_ID BIGINT NOT NULL,
    PROMPT_ID BIGINT(20) NOT NULL,
    LAST_SHOWN DATETIME NOT NULL,
    PRIMARY KEY (USER_ID, PROMPT_ID),
    FOREIGN KEY (PROMPT_ID) REFERENCES LOGIN_PROMPT(ID)
);

-- !Downs
DROP TABLE USER_PROMPT_VIEW;