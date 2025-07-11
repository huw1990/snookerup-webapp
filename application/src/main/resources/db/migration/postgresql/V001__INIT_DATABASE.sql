create table SCORE
(
    ID BIGSERIAL not null primary key,
    ROUTINE_ID VARCHAR(70) not null,
    PLAYER_USERNAME VARCHAR(255) not null,
    DATE_OF_ATTEMPT TIMESTAMP not null,
    LOOP BOOLEAN,
    CUSHION_LIMIT INTEGER,
    UNIT_NUMBER INTEGER,
    POT_IN_ORDER BOOLEAN,
    STAY_ON_ONE_SIDE_OF_TABLE BOOLEAN,
    BALL_STRIKING VARCHAR(30),
    NOTE VARCHAR(255),
    SCORE_VALUE INTEGER
);
