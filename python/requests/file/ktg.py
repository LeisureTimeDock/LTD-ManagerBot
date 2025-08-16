# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtRequestGenerator as kg
def main():
    api_paths = [
        "/move_group_file",               # 移动群文件
        "/trans_group_file",              # 转存为永久文件
        "/rename_group_file",             # 重命名群文件
        "/upload_group_file",             # 上传群文件
        "/create_group_file_folder",      # 创建群文件文件夹
        "/delete_group_file",             # 删除群文件
        "/delete_group_folder",           # 删除群文件夹
        "/get_group_file_system_info",    # 获取群文件系统信息
        "/get_group_root_files",          # 获取群根目录文件列表
        "/get_group_files_by_folder",     # 获取群子目录文件列表
        "/get_group_file_url",             # 获取群文件链接
        
        "/upload_private_file",           # 上传私聊文件
        "/get_private_file_url",           # 获取私聊文件链接
        
        "/get_file",                      # 获取文件信息（通用）
        "/download_file",                 # 下载文件到缓存目录
        "/clean_cache"                    # 清空缓存
    ]
    kg.generateRequestKt("file", "AbstractFile", api_paths)
if __name__ == "__main__":
    main()