# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtEventGenerator as kg
def main():
    # Kotlin事件类模板
    kotlin_template_system = """
package top.r3944realms.ltdmanager.napcat.events.{path}

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
* {event_description}事件
* @property data 响应数据
*/
@Serializable
data class {class_name}(
    
) : {super_class}Event() {{
    
    override fun subtype(): String {{
        return "{original_name}"
    }}
}}
    """
    api_paths = [
            # 账号控制
        "/get_robot_uin_range",    # 获取机器人可操作的账号范围（多账号托管时使用）
        "/bot_exit",               # 强制退出当前机器人账号（主动下线）

        # 协议级操作
        "/send_packet",            # 发送原始协议数据包（需熟悉QQ协议格式）
        "/nc_get_packet_status",   # 查询自定义数据包的状态（如发送结果、回包状态）

        # 系统信息
        "/get_version_info"        # 获取机器人框架/客户端的版本信息
    ]
    kg.generateEventKt("system", "AbstractSystem", api_paths, kotlin_template_system)
if __name__ == "__main__":
    main()