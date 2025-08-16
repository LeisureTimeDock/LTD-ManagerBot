# ktg.py
import sys
sys.path.append("..")  # 添加父目录到模块搜索路径
import KtEventGenerator as kg
def main():
    group_apis = [
    "/set_group_search",
    "/get_group_detail_info",
    "/set_group_add_option",
    "/set_group_robot_add_option",
    "/set_group_kick_members",
    "/set_group_remark",
    "/set_group_kick",
    "/get_group_system_msg",
    "/set_group_ban",
    "/get_essence_msg_list",
    "/set_group_whole_ban",
    "/set_group_portrait",
    "/set_group_admin",
    "/set_group_card",
    "/set_essence_msg",
    "/set_group_name",
    "/delete_essence_msg",
    "/set_group_leave",
    "/_send_group_notice",
    "/set_group_special_title",
    "/_get_group_notice",
    "/set_group_add_request",
    "/get_group_info",
    "/get_group_list",
    "/_del_group_notice",
    "/get_group_member_info",
    "/get_group_member_list",
    "/get_group_honor_info",
    "/get_group_info_ex",
    "/get_group_at_all_remain",
    "/get_group_shut_list",
    "/get_group_ignored_notifies",
    "/set_group_sign",
    "/send_group_sign"
    ]
    kg.generateEventKt("group", "AbstractGroup", group_apis)
if __name__ == "__main__":
    main()