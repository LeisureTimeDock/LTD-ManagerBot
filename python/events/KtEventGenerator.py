# KtEventGenerator.py
import os

# Kotlin事件类模板
kotlin_template_common = """
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

    
) : {super_class}Event(status0, retcode0, message0, wording0, echo0) {{
    
    override fun subtype(): String {{
        return "{original_name}"
    }}
}}
"""

def path_to_class_name(api_path):
    """将API路径转换为类名"""
    # 移除斜杠和前缀下划线
    clean_path = api_path.lstrip('/').lstrip('_').replace('.','point_')
    # 分割单词并转换为驼峰命名
    parts = clean_path.split('_')
    return ''.join(part.capitalize() for part in parts) + "Event"

def path_to_original_name(api_path):
    """获取原始API名称（不带斜杠）"""
    return api_path.lstrip('/')

def generate_description(class_name):
    """生成事件描述"""
    return class_name.replace("Event", "")

def generate_kotlin_class(api_path, path0, super_class, template):
    """生成完整的Kotlin类(通过自通过模板)"""
    class_name = path_to_class_name(api_path)
    original_name = path_to_original_name(api_path)
    event_description = generate_description(class_name)
    
    return template.format(
        path=path0,
        class_name=class_name,
        original_name=original_name,
        event_description=event_description,
        super_class=super_class
    )

def find_project_root(start_path=None, marker='.projectroot'):
    """向上查找直到找到标记文件"""
    if start_path is None:
        start_path = os.path.abspath(__file__)
    
    current = os.path.dirname(start_path)
    while True:
        if marker in os.listdir(current):
            return current
        parent = os.path.dirname(current)
        if parent == current:  # 到达根目录
            raise FileNotFoundError(f"Project root marker '{marker}' not found")
        current = parent


def generateEventKt(path0, superclass0, apipaths, template = kotlin_template_common):
    """
    生成事件类文件
    :param path0: 包路径，如"account"或"message"
    :param superclass0: 父类名，如"AbstractAccount"或"AbstractMessage"
    :param apipaths: API路径列表
    :param template: 自提供模板(不提供则使用默认)
    """
    # 创建输出目录
    root_path = find_project_root()
    
    path_ = path0.replace(".", "/")
    output_dir = os.path.join(
        root_path,
        "src/main/kotlin/top/r3944realms/ltdmanager/napcat/events",
        path_,
        "wip"
    )
    os.makedirs(output_dir, exist_ok=True)
    
    # 为每个API路径生成Kotlin文件
    for api_path in apipaths:
        kotlin_code = generate_kotlin_class(api_path, path0, superclass0, template)
        class_name = path_to_class_name(api_path)
        filename = os.path.join(output_dir, f"{class_name}.kt")

        with open(filename, "w", encoding="utf-8") as f:
            f.write(kotlin_code)

        print(f"Generated: {filename}")

    print(f"\nSuccessfully generated {len(apipaths)} Kotlin event classes in '{output_dir}' directory")

# 确保方法可以被导入
__all__ = ['generateEventKt']