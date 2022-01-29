package discord_bot.utility;

import discord_bot.common.Constants;
import discord_bot.common.ContactBot;
import discord_bot.service.RunShellScript;

public class ServerUtil {
	
	/*
	 *  サーバを起動する
	 */
	public static void start() {
		RunShellScript RunShellScript = new RunShellScript();
		RunShellScript.run(new String[] {"/minecraft/bat/job/discord_boot.sh"});
		if (RunShellScript.returnCode == 0) {
			
		} else {
			
		}
		
		ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage(RunShellScript.getResultString()).queue();
		
	}
	
	/*
	 *  サーバを停止する
	 */
	public static void stop() {
		/*
		RunShellScript RunShellScript = new RunShellScript();
		RunShellScript.run(new String[] {"/minecraft/bat/job/discord_boot.sh"});
		if (RunShellScript.returnCode == 0) {
			
		} else {
			
		}
		*/
		
//		ContactBot.jda.getTextChannelById(Constants.CHANNEL_ID).sendMessage(RunShellScript.getResultString()).queue();
		
		ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage("未実装").queue();
		
	}
	
	/*
	 *  サーバの状態を確認する
	 */
	public static void status() {
		/*
		RunShellScript RunShellScript = new RunShellScript();
		RunShellScript.run(new String[] {"/minecraft/bat/job/discord_boot.sh"});
		if (RunShellScript.returnCode == 0) {
			
		} else {
			
		}
		*/
		
//		ContactBot.jda.getTextChannelById(Constants.CHANNEL_ID).sendMessage(RunShellScript.getResultString()).queue();
		
		ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage("未実装").queue();
		
	}

}
