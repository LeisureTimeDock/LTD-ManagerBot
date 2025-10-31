package top.r394realms.ltdmanagertest.msg

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.event.group.GetGroupMemberListEvent
import top.r3944realms.ltdmanager.napcat.request.group.GetGroupMemberListRequest
import top.r3944realms.ltdmanager.napcat.request.message.SetMsgEmojiLikeRequest

fun main() = GlobalManager.runBlockingMain {
//    val getGroupMemberListEvent = GlobalManager.napCatClient.send<GetGroupMemberListEvent>(
//        GetGroupMemberListRequest(
//            ID.long(920719236),
//            false
//        )
//    )
//    println(getGroupMemberListEvent.data.filter { !it.isRobot }.map { it.userId to it.nickname }.toMap())
    for (i in 61  ..81){
        GlobalManager.napCatClient.sendUnit(
            SetMsgEmojiLikeRequest(
                i.toDouble(), ID.long(2080109145), true
            )
        )
    }


}