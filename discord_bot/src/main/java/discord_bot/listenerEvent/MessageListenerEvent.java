package discord_bot.listenerEvent;

import discord_bot.common.ChatMessage;
import discord_bot.common.Constants;
import discord_bot.service.ChatUserCheckService;
import discord_bot.service.LogCheckService;
import discord_bot.utility.ChatLogUtil;
import discord_bot.utility.MinecraftUtil;
import discord_bot.utility.ServerUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageListenerEvent {
	
	/*
	 *  メッセージを取得し、そのメッセージに対応する処理を実行する
	 *  
	 *  @param MessageReceivedEvent event
	 */
	public static void getMessageToFunction(MessageReceivedEvent event) {
		// 送信されたメッセージを取得
		String message = event.getMessage().getContentRaw();

		// コマンドチャンネルの場合
		if (event.getChannel().getId().equals(Constants.COMMANDCHANNEL_ID)) {

			// help : コマンド一覧
			if (message.startsWith("help")) {
				help(event);
				return;
			}

			// server start : server起動シェルの実行
			if (message.startsWith("server start")) {
				ServerUtil.start();
				return;
			}

			// list : サーバ内のplayerのリストを表示
			if (message.startsWith("list")) {
				MinecraftUtil.list();
				return;
			}

			// sendmsg_on : Discordチャットの転送開始
			if (message.startsWith("sendmsg_on")) {
				setSendDiscordMessage(event, true);
				return;
			}
			// sendmsg_off : Discordチャットの転送停止
			if (message.startsWith("sendmsg_off")) {
				setSendDiscordMessage(event, false);
				return;
			}

			// login_on : ログイン通知の開始
			if (message.startsWith("login_on")) {
				setLoginMessage(event, true);
				return;
			}
			// login_off : ログイン通知の停止
			if (message.startsWith("login_off")) {
				setLoginMessage(event, false);
				return;
			}

			// chat_on : ゲーム内チャットのログ出力開始
			if (message.startsWith("chat_on")) {
				setChatLog(event, true);
				return;
			}
			// chat_off : ゲーム内チャットのログ出力停止
			if (message.startsWith("chat_off")) {
				setChatLog(event, false);
				return;
			}
		}
		// チャットチャンネルの場合
		if (event.getChannel().getId().equals(Constants.CHATCHANNEL_ID)) {
			getDiscordMessage(event);
		}
		return;
	}
	
	/*
	 *  メッセージコマンドの一覧を表示する
	 */
	public static void help(MessageReceivedEvent event) {
		
		String str = "";
		str += "server start:マインクラフトサーバの起動\n";
		str += "list:ログイン中のプレイヤーリストを表示\n";
		str += "sendmsg_on:Discordチャットの転送開始\n";
		str += "sendmsg_off:Discordチャットの転送の停止\n";
		str += "login_on:ログイン通知の開始\n";
		str += "login_off:ログイン通知の停止\n";
		str += "chat_on:ゲーム内チャットのログ出力開始\n";
		str += "chat_off:ゲーム内チャットのログ出力停止\n";
		
		// テキスト表示
		event.getTextChannel().sendMessage(str).queue();
	}
	
	/*
	 *  ログイン通知の設定を行う
	 *  	login_on:ログイン通知の開始
	 *  	login_off:ログイン通知の停止
	 */
	public static void setLoginMessage(MessageReceivedEvent event, boolean flg) {

		LogCheckService.setLoginCheckFlag(flg);
		
		if (flg) {
			event.getTextChannel().sendMessage("ログイン通知の開始").queue();
		} else {
			event.getTextChannel().sendMessage("ログイン通知の停止").queue();
		}
	}
	
	/*
	 *  ゲーム内チャットの表示の設定を行う
	 *  	chat_on:ゲーム内チャットのログ出力の開始
	 *  	chat_off:ゲーム内チャットのログ出力の停止
	 */
	public static void setChatLog(MessageReceivedEvent event, boolean flg) {

		LogCheckService.setChatLogCheckFlag(flg);
		
		if (flg) {
			event.getTextChannel().sendMessage("ゲーム内チャットのログ出力の開始").queue();
		} else {
			event.getTextChannel().sendMessage("ゲーム内チャットのログ出力の停止").queue();
		}
	}
	
	/*
	 *  Discordチャットの転送設定を行う
	 *  	sendmsg_on:Discordチャットの転送開始
	 *  	sendmsg_off:Discordチャットの転送停止
	 */
	public static void setSendDiscordMessage(MessageReceivedEvent event, boolean flg) {

		if (flg) {
			if (ChatUserCheckService.setChatUser(event.getAuthor().getId(), event.getAuthor().getName())) {
				event.getTextChannel().sendMessage("入力チャットのマインクラフト転送の開始").queue();
				return;
			}
		} else {
			if (ChatUserCheckService.deleteChatUser(event.getAuthor().getId())) {
				event.getTextChannel().sendMessage("入力チャットのマインクラフト転送の停止").queue();
				return;
			}
		}
		
		event.getTextChannel().sendMessage("処理に失敗").queue();
	}
	
	/*
	 *  Discordチャットチャンネルのメッセージをマインクラフトに送信
	 */
	public static void getDiscordMessage(MessageReceivedEvent event) {

		// ユーザチェック
		if (!ChatUserCheckService.isChatUser(event.getAuthor().getId())) {
			// 対象ユーザではないため処理終了
			return;
		}
		
		// /say : ゲーム内チャットへメッセージを送信
		ChatMessage chatmessage = new ChatMessage();
		chatmessage.userName = event.getAuthor().getName();
		chatmessage.message = event.getMessage().getContentRaw();
		if (chatmessage.userName.isEmpty() || chatmessage.message.isEmpty()) {
			event.getTextChannel().sendMessage("メッセージの送信に失敗").queue();
		}
		ChatLogUtil.sendDiscordChat(chatmessage).getMessage();
		// オリジナルメッセージの削除
		
		// 成形したメッセージの表示
		event.getTextChannel().sendMessage("[" + chatmessage.userName + "] " + chatmessage.message).queue();
		
	}
}
