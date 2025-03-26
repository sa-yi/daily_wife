package com.sayi.daily_wife;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.action.response.StrangerInfoResp;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import java.util.*;

import static com.mikuac.shiro.core.BotPlugin.MESSAGE_BLOCK;
import static com.mikuac.shiro.core.BotPlugin.MESSAGE_IGNORE;

public class DailyWifeFunction {
    private final Map<Long,Long> wifeMap=new HashMap<>();


    public void refreshWifeMap(){
        wifeMap.clear();
    }

    Random random = new Random();
    public int fetchDailyWife(Bot bot,GroupMessageEvent event){
        long userId=event.getUserId();
        long groupId=event.getGroupId();

        Long userWife=wifeMap.get(userId);

        String retMsg;
        if(userWife==null) {
            ActionList<GroupMemberInfoResp> memberInfoRespList = bot.getGroupMemberList(groupId, false);
            if (memberInfoRespList.getRetCode() != 0) {
                retMsg = MsgUtils.builder().reply(event.getMessageId()).text("抽老婆失败！").build();
                bot.sendGroupMsg(event.getGroupId(), retMsg, false);
                return MESSAGE_IGNORE;
            }
            List<GroupMemberInfoResp> data = memberInfoRespList.getData();
            GroupMemberInfoResp resp = data.get(random.nextInt(data.size()));
            long wifeId = resp.getUserId();
            userWife=wifeId;
            wifeMap.put(userId,wifeId);
        }
        ActionData<GroupMemberInfoResp> wifeInfo = bot.getGroupMemberInfo(groupId, userWife, true);
        if (wifeInfo.getRetCode() != 0) {
            retMsg = MsgUtils.builder().reply(event.getMessageId()).text("获取老婆信息失败！").build();
        } else {
            String wifeName = wifeInfo.getData().getNickname();
            wifeName+="("+userWife+")";
            String wifeAvatar = ShiroUtils.getUserAvatar(userWife, 640);
            retMsg = MsgUtils.builder().reply(event.getMessageId()).at(userId).text("你今天的群老婆是").img(wifeAvatar).text("亲爱的").text(wifeName).build();
        }

        bot.sendGroupMsg(event.getGroupId(), retMsg, false);
        return MESSAGE_BLOCK;
    }

    public int refreshDailyWife(Bot bot,GroupMessageEvent event){
        long userId=event.getUserId();
        long groupId=event.getGroupId();

        Long userWife=wifeMap.get(userId);

        String retMsg;
        if(userWife==null) {
            bot.sendGroupMsg(groupId,"你还没有老婆！先抽一个吧！",false);
            return MESSAGE_BLOCK;
        }
        ActionList<GroupMemberInfoResp> memberInfoRespList = bot.getGroupMemberList(groupId, false);
        if (memberInfoRespList.getRetCode() != 0) {
            retMsg = MsgUtils.builder().reply(event.getMessageId()).text("换老婆失败！").build();
            bot.sendGroupMsg(event.getGroupId(), retMsg, false);
            return MESSAGE_IGNORE;
        }
        List<GroupMemberInfoResp> data = memberInfoRespList.getData();
        GroupMemberInfoResp resp = data.get(random.nextInt(data.size()));
        long wifeId = resp.getUserId();
        userWife=wifeId;
        wifeMap.put(userId,wifeId);

        ActionData<GroupMemberInfoResp> wifeInfo = bot.getGroupMemberInfo(groupId, userWife, true);
        if (wifeInfo.getRetCode() != 0) {
            retMsg = MsgUtils.builder().reply(event.getMessageId()).text("获取新的老婆信息失败！").build();
        } else {
            String wifeName = wifeInfo.getData().getNickname();
            wifeName+="("+userWife+")";
            String wifeAvatar = ShiroUtils.getUserAvatar(userWife, 640);
            retMsg = MsgUtils.builder().reply(event.getMessageId()).at(userId).text("渣男！给你换了").img(wifeAvatar).text("亲爱的").text(wifeName).text("当新的老婆！").build();
        }
        bot.sendGroupMsg(event.getGroupId(), retMsg, false);
        return MESSAGE_BLOCK;
    }

    public int fetchWifeList(Bot bot,GroupMessageEvent event){
        long groupId=event.getGroupId();
        StringBuilder msgBuilder=new StringBuilder();
        msgBuilder.append("当前群老婆列表：");
        for(Map.Entry<Long,Long> wifeEntry:wifeMap.entrySet()){
            long userId=wifeEntry.getKey();
            long wifeId=wifeEntry.getValue();
            ActionData<GroupMemberInfoResp> memberInfo=bot.getGroupMemberInfo(groupId,userId,true);
            if(memberInfo.getRetCode()!=0){//从该群组获取该用户失败，表面该用户不在此群组中
                continue;
            }
            ActionData<StrangerInfoResp> wifeInfo=bot.getStrangerInfo(wifeId,false);
            if(wifeInfo.getRetCode()!=0){//获取老婆用户信息失败
                continue;
            }
            GroupMemberInfoResp memberInfoResp=memberInfo.getData();
            String userName=memberInfoResp.getNickname();

            StrangerInfoResp wifeInfoResp =wifeInfo.getData();
            String wifeName= wifeInfoResp.getNickname();

            msgBuilder
                    .append("\n\n")
                    .append(userName)
                    .append("(")
                    .append(userId)
                    .append(")")
                    .append("------->")
                    .append(wifeName)
                    .append("(")
                    .append(wifeId)
                    .append(")");
        }
        String msg=msgBuilder.toString();
        Map<String, Object> singleMsg=ShiroUtils.generateSingleMsg(bot.getSelfId(),"老婆列表",msg);
        List<Map<String,Object>> msgs=new ArrayList<>(){{add(singleMsg);}};
        bot.sendGroupForwardMsg(groupId,msgs);
        return MESSAGE_BLOCK;
    }
}
