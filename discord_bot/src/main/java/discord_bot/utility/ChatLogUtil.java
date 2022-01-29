package discord_bot.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import discord_bot.common.ChatMessage;
import discord_bot.common.Constants;
import discord_bot.common.ContactBot;
import discord_bot.common.LoginStatus;
import discord_bot.service.LogCheckService;
import discord_bot.service.RunShellScript;

public class ChatLogUtil {
	
	// ゲーム内チャット判定用正規表現定義
	// チャット判定用正規表現
	private static final Pattern IN_GAME_CHAT = Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}]\\s\\[Server\\sthread/INFO\\]:\\s<(.*?)>\\s(.*)");
	// チャット発言ユーザ判定用正規表現
	private static final Pattern IN_GAME_CHAT_USER = Pattern.compile("<(.*?)>");

	
	public static LoginStatus sendLogInMessage(String line) {
		
		LoginStatus loginstatus = new LoginStatus();
		// ログイン:joined the game
		// ログアウト:left the game
		int endIndex_login = line.indexOf("joined the game");
		int endIndex_logout = line.indexOf("left the game");
		
		if (endIndex_login == -1 && endIndex_logout == -1) {
			loginstatus.result = false;
			return loginstatus;
		} else if (endIndex_login != -1) {
			loginstatus.status = "login";
			loginstatus.userName = line.substring(line.indexOf("[Server thread/INFO]:") + 21, endIndex_login);
			ContactBot.jda.getTextChannelById(Constants.CHATCHANNEL_ID).sendMessage(loginstatus.userName + "がログインしました。").queue();
		} else if (endIndex_logout != -1) {
			loginstatus.status = "logout";
			loginstatus.userName = line.substring(line.indexOf("[Server thread/INFO]:") + 21, endIndex_logout);
			ContactBot.jda.getTextChannelById(Constants.CHATCHANNEL_ID).sendMessage(loginstatus.userName + "がログアウトしました。").queue();
		}
		loginstatus.result = true;
		return loginstatus;
	}
	
	/*
	 *  ゲーム内チャット判定とメッセージ出力
	 *  [23:54:06] [Server thread/INFO]: <shula> [name:"森の洋館", x:-1116, y:72, z:-490, dim:minecraft:overworld]
	 */
	public static ChatMessage sendInGameChat(String line) {
		
		// メッセージユーザチェック
		
		
		ChatMessage chatmessage = new ChatMessage();
		chatmessage.result = false;
		Matcher inGameChat = IN_GAME_CHAT.matcher(line);
		Matcher inGameChatUser = IN_GAME_CHAT_USER.matcher(line);
		if (inGameChat.find()) {
			// ユーザのチャット
			// ユーザ以降の切り出し
			if (inGameChatUser.find()) {
				int begin = inGameChatUser.start();
				if (begin != -1) {
					chatmessage.result = true;
					chatmessage.message = line.substring(begin);
					ContactBot.jda.getTextChannelById(Constants.CHATCHANNEL_ID).sendMessage(chatmessage.message).queue();
				}
			}
		}
		return chatmessage;
	}
	
	/*
	 *  Discord内チャットをゲーム内チャットに転送
	 * 		tellraw @a {"text": "<User名> チャット内容"}
	 * 		screen -p 0 -S minecraft -X stuff 'tellraw @a {"text": "<test> testchat"}\015'
	 */
	public static ChatMessage sendDiscordChat(ChatMessage chatmessage) {
		boolean isStatus = LogCheckService.getStatus();
		if (!isStatus) {
			chatmessage.result = false;
			return chatmessage;
		}
		RunShellScript RunShellScript = new RunShellScript();
		RunShellScript.run(new String[] 
				{"screen","-p","0","-S","minecraft","-X","stuff",
						"tellraw @a {\"text\": \"<" + chatmessage.userName + "> " + chatmessage.message + "\"}\\015"});
		
		chatmessage.result = RunShellScript.result;
		if (!chatmessage.result) {
			// 実行エラーで出力
			System.out.println("実行エラー:" + RunShellScript.shellPath);
		}
		return chatmessage;
	}

}


 
 
