# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtRequestGenerator as kg
def main():
    api_paths = [
       # 群信息获取
        "/get_group_info",
        "/get_group_info_ex",
        "/get_group_detail_info",
        "/get_group_list",
        "/get_group_member_info",
        "/get_group_member_list",
        "/get_group_honor_info",
        "/get_group_at_all_remain",
        "/get_group_shut_list",
        "/get_group_ignored_notifies",
        "/get_group_system_msg",
        "/get_essence_msg_list",

        # 群设置
        "/set_group_name",
        "/set_group_portrait",
        "/set_group_search",
        "/set_group_add_option",
        "/set_group_robot_add_option",
        "/set_group_remark",
        "/set_group_card",
        "/set_group_special_title",
        "/set_group_admin",
        "/set_group_leave",

        # 群消息管理
        "/set_essence_msg",
        "/delete_essence_msg",
        "/_send_group_notice",
        "/_get_group_notice",
        "/_del_group_notice",

        # 群成员管理
        "/set_group_kick",
        "/set_group_kick_members",
        "/set_group_ban",
        "/set_group_whole_ban",
        "/set_group_sign",
        "/send_group_sign",

        # 加群请求
        "/set_group_add_request"
    ]
    kg.generateRequestKt("group", "AbstractGroup", api_paths)
if __name__ == "__main__":
    main()