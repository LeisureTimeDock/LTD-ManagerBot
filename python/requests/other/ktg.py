# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import requests.KtRequestGenerator as kg
def main():
    api_paths = [
        # 保留
        "/send_private_msg",                # send_private_msg
        "/send_group_msg",                  # send_group_msg
        "/send_msg",                        # send_msg

        # 接口
        "/unknown",                         # unknown
        "/get_guild_list",                  # get_guild_list
        "/get_guild_service_profile",       # get_guild_service_profile
        "/check_url_safely",                # 检查链接安全性
        
        #bug
        "/get_collection_list",             # 获取收藏列表
        "/get_group_ignore_add_request"     # 获取被过滤的加群请求
    ]
    kg.generateRequestKt("other", "AbstractOther", api_paths)
if __name__ == "__main__":
    main()