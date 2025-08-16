# ktg.py
import sys
sys.path.append("../..")  # 添加父目录到模块搜索路径
import KtEventGenerator as kg
def main():
    api_paths = [
        "/send_private_msg",           # 发送私聊消息（文本、图片、表情、JSON、语音、视频、回复、音乐卡片等）
        "/send_private_forward_msg",   # 发送私聊合并转发消息
        "/forward_friend_single_msg",  # 转发单条消息到私聊
        "/friend_poke"                 # 发送私聊戳一戳
    ]
    kg.generateEventKt("message.personal", "AbstractMessage", api_paths)
if __name__ == "__main__":
    main()