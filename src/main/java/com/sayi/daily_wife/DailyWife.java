package com.sayi.daily_wife;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyWife extends BotPlugin {

    DailyWifeFunction function = new DailyWifeFunction();

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        String msg = event.getMessage();
        if (msg.equals("#抽老婆")) {
            return function.fetchDailyWife(bot, event);
        } else if (msg.equals("#换老婆")) {
            return function.refreshDailyWife(bot, event);
        } else if (msg.equals("#老婆列表")) {
            return function.fetchWifeList(bot, event);
        }
        return MESSAGE_IGNORE;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private void refreshWife() {//每天凌晨清空老婆列表
        function.refreshWifeMap();
    }
}
