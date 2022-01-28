package discord_bot.service;

import discord_bot.common.Constants;
import discord_bot.common.ContactBot;
import discord_bot.listenerEvent.MessageListenerEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/*
 *  
 */
public class MessageReceivedService extends ListenerAdapter {

	public void start() throws Exception {
		//リスナーイベントの起動
		ContactBot.jda.addEventListener(new MessageReceivedService());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		// Bot自身のメッセージのため終了
		if (event.getAuthor().equals(ContactBot.jda.getSelfUser())) {
			return;
		}
		
		// 特定のテキストチャンネルのみ反応
		if (event.getChannel().getId().equals(Constants.COMMANDCHANNEL_ID)
				|| event.getChannel().getId().equals(Constants.CHATCHANNEL_ID)) {
			// コマンド用CH
			MessageListenerEvent.getMessageToFunction(event);
		} else {
			// 指定チャンネル以外のため終了
			return;
		}
	}
}
