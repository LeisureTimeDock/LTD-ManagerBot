# ktg.py
import sys
sys.path.append("../..")  # 添加父目录到模块搜索路径
import KtEventGenerator as kg
def main():
    api_paths = [
        "/send_group_msg",              # 发送群消息（文本、图片、表情、JSON、语音、视频、回复、音乐卡片等）
        "/send_group_forward_msg",      # 发送群合并转发消息
        "/forward_group_single_msg",    # 转发单条消息到群
        "/group_poke"                   # 发送群聊戳一戳
    ]
    kg.generateEventKt("message.group", "AbstractMessage", api_paths)
if __name__ == "__main__":
    main()