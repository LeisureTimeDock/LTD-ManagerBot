# NapCat
将回应抽象为event模型
将请求抽象为request模型
## InvitationCodesModule 模块设计时序表
```mermaid
sequenceDiagram
    participant User as 用户
    participant Bot as 机器人
    participant DB as 数据库视图/表
    participant API as Token API

    User->>Bot: 发送消息触发关键词
    Bot->>DB: 根据QQ查询 qualified_user_info 获取id, effective, is_used, token
    alt id不存在
        DB-->>Bot: 无记录
        Bot-->>User: 提示无法查询id，请联系管理员
    else id存在
        alt effective=1 && is_used=1
            Bot-->>User: 提示邀请码已使用，勿重复发送
        else effective=1 && is_used=0
            DB-->>Bot: 返回token
            Bot->>User: 构造邮件并发送token
            Bot-->>User: 邮件已发送
        else effective=0
            Bot->>API: 请求生成新Token
            alt API返回 success=false
                API-->>Bot: 返回错误信息
                Bot-->>User: 提示API错误消息
            else API返回 success=true
                API-->>Bot: 返回新Token
                Bot->>User: 构造邮件并发送新Token
                Bot-->>User: 邮件已发送
                Bot->>DB: 查询邀请码数据库获取 token_id
                DB-->>Bot: 返回 token_id
                Bot->>DB: 写入/更新 invitation_code_ascription 映射
            end
        end
    end

````