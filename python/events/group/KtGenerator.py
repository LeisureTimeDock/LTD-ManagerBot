import os

# 原始API路径列表
api_paths = [
    "/set_qq_avatar",
    "/send_like",
    "/mark_private_msg_as_read",
    "/mark_group_msg_as_read",
    "/create_collection",
    "/set_friend_add_request",
    "/set_self_longnick",
    "/get_login_info",
    "/get_recent_contact",
    "/get_stranger_info",
    "/get_friend_list",
    "/_mark_all_as_read",
    "/get_profile_like",
    "/fetch_custom_face",
    "/delete_friend",
    "/_get_model_show",
    "/_set_model_show",
    "/nc_get_user_status",
    "/get_status",
    "/get_mini_app_ark",
    "/get_unidirectional_friend_list",
    "/set_diy_online_status",
    "/set_friend_remark"
]

# Kotlin事件类模板
kotlin_template = """
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * {event_description}事件
 * @property data 响应数据
 */
@Serializable
data class {class_name}(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {{
    
    override fun subtype(): String {{
        return "{original_name}"
    }}
}}
"""

def path_to_class_name(api_path):
    """将API路径转换为类名"""
    # 移除斜杠和前缀下划线
    clean_path = api_path.lstrip('/').lstrip('_')
    # 分割单词并转换为驼峰命名
    parts = clean_path.split('_')
    return ''.join(part.capitalize() for part in parts) + "Event"

def path_to_original_name(api_path):
    """获取原始API名称（不带斜杠）"""
    return api_path.lstrip('/')

def generate_description(class_name):
    """生成事件描述"""
    return class_name.replace("Event", "")

def generate_kotlin_class(api_path):
    """生成完整的Kotlin类"""
    class_name = path_to_class_name(api_path)
    original_name = path_to_original_name(api_path)
    event_description = generate_description(class_name)

    return kotlin_template.format(
        class_name=class_name,
        original_name=original_name,
        event_description=event_description
    )

def main():
    # 创建输出目录
    output_dir = "../../../src/main/kotlin/top/r3944realms/ltdmanager/napcat/events/group/wip"
    os.makedirs(output_dir, exist_ok=True)

    # 为每个API路径生成Kotlin文件
    for api_path in api_paths:
        kotlin_code = generate_kotlin_class(api_path)
        class_name = path_to_class_name(api_path)
        filename = f"{output_dir}/{class_name}.kt"

        with open(filename, "w", encoding="utf-8") as f:
            f.write(kotlin_code)

        print(f"Generated: {filename}")

    print(f"\nSuccessfully generated {len(api_paths)} Kotlin event classes in '{output_dir}' directory")

if __name__ == "__main__":
    main()