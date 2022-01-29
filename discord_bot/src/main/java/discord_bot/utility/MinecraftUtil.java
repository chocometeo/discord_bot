package discord_bot.utility;

import java.util.List;

import discord_bot.common.Constants;
import discord_bot.common.ContactBot;
import discord_bot.service.RunShellScript;

public class MinecraftUtil {
	
	public static void list() {
		
		// 処理
		RunShellScript RunShellScript = new RunShellScript();
		RunShellScript.run(new String[] {"/minecraft/bat/job/discord_playerList.sh"});
		
		if (RunShellScript.returnCode == 0) {
			List<String> playerList = PlayerList.getPlayerList(RunShellScript.resultStr);
			
			if (playerList == null) {
				ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage("ログ取得エラー").queue();
			} else if (playerList.isEmpty()) {
				ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage("ログイン中のプレイヤーはいませんでした。").queue();
			} else {
				String message = "";
				for (String s : playerList) {
					message += s + "\r\n";
				}
				ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage(message).queue();
			}
		} else if (RunShellScript.returnCode == 1) {
			ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage("サーバが起動していません").queue();
		} else {
			ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage("PlayerListの取得に失敗").queue();
		}
	}

}
