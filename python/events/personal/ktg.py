# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtEventGenerator as kg
def main():
    api_paths = [
        "/ocr_image",                       # OCR 图片识别
        "/.ocr_image",                      # .OCR 图片识别
        "/translate_en2zh",                 # 获取 CSRF Token
        "/.handle_quick_operation",         # .对事件执行快速操作
        "/can_send_image",                  # 检查是否可以发送图片
        "/can_send_record",                 # 检查是否可以发送语音
        "/get_ai_characters",               # 获取AI语音人物
        "/click_inline_keyboard_button",    # 点击按钮
        "/get_ai_record",                   # 获取AI语音
        "/set_input_status"                 # 设置输入状态
    ]
    kg.generateEventKt("personal", "AbstractPersonal", api_paths)
if __name__ == "__main__":
    main()