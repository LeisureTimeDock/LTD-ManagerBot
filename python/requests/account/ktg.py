# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtEventGenerator as kg
def main():
    api_paths = [
        # 账号信息管理
        "/set_qq_profile",          # 设置账号信息
        "/set_qq_avatar",           # 设置头像
        "/set_self_longnick",       # 设置个性签名
        "/get_login_info",          # 获取登录号信息
        "/get_stranger_info",       # 获取账号信息（陌生人）
        "/get_online_clients",      # 获取在线客户端列表
        "/send_like",               # 点赞
        "/get_profile_like",        # 获取点赞列表
        "/create_collection",       # 创建收藏
        "/fetch_custom_face",       # 获取收藏表情
        "/get_recent_contact",      # 获取最近消息列表

        # 在线状态管理
        "/set_online_status",       # 设置在线状态（10=在线，60=Q我）
        "/set_diy_online_status",   # 设置自定义在线状态
        "/get_status",              # 获取状态（基础）
        "/nc_get_user_status",      # 获取用户状态（扩展）
        "/_get_model_show",         # 获取在线机型
        "/_set_model_show",         # 设置在线机型
        
        # 好友关系管理
        "/get_friend_list",                # 获取好友列表
        "/get_unidirectional_friend_list", # 获取单向好友列表
        "/get_friends_with_category",      # 获取好友分组列表
        "/set_friend_remark",              # 设置好友备注
        "/delete_friend",                  # 删除好友
        "/set_friend_add_request",         # 处理好友请求
        "/get_doubt_friends_add_request",  # 获取被过滤好友请求
        "/set_doubt_friends_add_request",  # 处理被过滤请求（调用即同意！）

        # 私聊消息状态
        "/mark_private_msg_as_read",       # 设置私聊已读
        
        # 消息状态控制
        "/mark_msg_as_read",         # 设置消息已读（通用）
        "/mark_group_msg_as_read",   # 设置群聊已读
        "/_mark_all_as_read",        # 设置所有消息已读
        
        # 卡片与分享功能
        "/ArkSharePeer",        # 获取推荐好友/群聊卡片
        "/ArkShareGroup",       # 获取推荐群聊卡片
        "/get_mini_app_ark",    # 获取小程序卡片
    ]
    kg.generateEventKt("account", "AbstractAccount", api_paths)
if __name__ == "__main__":
    main()