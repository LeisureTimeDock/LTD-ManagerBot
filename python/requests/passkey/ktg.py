# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtRequestGenerator as kg
def main():
    api_paths = [
        "/get_clientkey",       # 获取 clientkey
        "/get_cookies",         # 获取 cookies
        "/get_csrf_token",      # 获取 CSRF Token
        "/get_credentials",     # 获取 QQ 相关接口凭证
        "/get_rkey",            # 获取 rkey（通用）
        "/nc_get_rkey",         # nc获取rkey（可能是特殊渠道）
        "/get_rkey_server"      # 获取rkey服务（可能是服务端专用）
    ]
    kg.generateRequestKt("passkey", "AbstractPassKey", api_paths)
if __name__ == "__main__":
    main()