# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtEventGenerator as kg
def main():
    api_paths = [
        "/send_poke",               # 发送戳一戳（通用版，可能同时适用于群和私聊）
        "/delete_msg",              # 撤回消息
        "/get_group_msg_history",   # 获取群历史消息
        "/get_friend_msg_history",  # 获取好友历史消息
        "/get_msg",                 # 获取消息详情
        "/get_forward_msg",         # 获取合并转发消息
        "/send_forward_msg",        # 发送合并转发消息（通用版）
        "/set_msg_emoji_like",      # 贴表情（点赞表情）
        "/fetch_emoji_like",        # 获取贴表情详情
        "/get_record",              # 获取语音消息详情
        "/get_image",               # 获取图片消息详情
        "/send_group_ai_record"     # 发送群AI语音
    ]
    kg.generateEventKt("message", "AbstractMessage", api_paths)
if __name__ == "__main__":
    main()