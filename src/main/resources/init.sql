DELIMITER //

CREATE DATABASE IF NOT EXISTS ltd_manager_bot //

USE ltd_manager_bot //

CREATE TABLE IF NOT EXISTS invitation_code_ascription(
    id int PRIMARY KEY REFERENCES minecraft_manager_ltd.players(id),
    token_id int unsigned NULL REFERENCES blessingskin.invitation_codes(id) )
//;
-- 也许token_id应该改名为code_id


DELIMITER ;

DELIMITER //
DROP VIEW IF EXISTS qualified_user_info //
CREATE VIEW qualified_user_info AS
SELECT
    p.id                      AS player_id,
    p.player_name             AS player_name,
    p.qq                      AS qq,
    ic.code                   AS token,
    ic.expires_at             AS expires_at,
    CASE
        WHEN ic.is_expired = 0 THEN 1               -- 未过期 → 有效
        WHEN ic.used_by != 0 THEN 1                 -- 已使用 → 有效
        ELSE 0                                     -- 过期且未使用 → 无效
        END                   AS effective,
    IF(ic.used_by != 0, 1, 0) AS is_used -- 是否使用

FROM (minecraft_manager_ltd.players p LEFT JOIN ltd_manager_bot.invitation_code_ascription ica ON p.id = ica.id) LEFT JOIN blessingskin.invitation_codes ic ON ica.token_id = ic.id
WHERE p.status = 1; //

DELIMITER ;
SELECT p1.id, invitation_codes.id
FROM minecraft_manager_ltd.players p1 LEFT JOIN blessingskin.players p2 ON LOWER(p1.player_name) COLLATE utf8mb4_unicode_ci = LOWER(p2.name) COLLATE utf8mb4_unicode_ci JOIN blessingskin.invitation_codes ON p2.uid = invitation_codes.used_by
WHERE used_by != 0; --
-- uid 拿到token 去查询 ID
