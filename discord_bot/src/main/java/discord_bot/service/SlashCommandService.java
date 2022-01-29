package discord_bot.service;

import discord_bot.common.ChatMessage;
import discord_bot.common.Constants;
import discord_bot.common.ContactBot;
import discord_bot.utility.ChatLogUtil;
import discord_bot.utility.MinecraftUtil;
import discord_bot.utility.ServerUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/*
 *  
 */
public class SlashCommandService extends ListenerAdapter {

	public void start() throws Exception {
		//リスナーイベントの起動
		ContactBot.jda.addEventListener(new SlashCommandService());
		
		// 登録コマンドの初期化
		ContactBot.jda.updateCommands().queue();

		// コマンドの登録

		// /startコマンドの実装
		ContactBot.jda.getGuildById(Constants.GUILD_ID).upsertCommand("server", "MinecraftServer の操作")
			.addOption(OptionType.STRING, "cmd", "■\"start\":サーバの起動 / ■stop\":サーバの停止 / ■\"status\":サーバの状態", true).queue();
		// /listコマンドの実装
		ContactBot.jda.getGuildById(Constants.GUILD_ID).upsertCommand("list", "プレイ中のプレイヤーを表示").queue();

		// /sendmsgコマンドの実装
		ContactBot.jda.getGuildById(Constants.GUILD_ID).upsertCommand("sendmsg", "Discordチャットの転送設定")
		.addOption(OptionType.STRING, "status", "■\"on\":メッセージの自動送信を有効 / ■off\":メッセージの自動送信を無効", true).queue();
				
		// /sayコマンドの実装
		ContactBot.jda.getGuildById(Constants.GUILD_ID).upsertCommand("say", "ゲーム内チャットへメッセージを送信")
			.addOption(OptionType.STRING, "msg", "メッセージの入力", true).queue();
		
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		if (!event.getChannel().getId().equals(Constants.COMMANDCHANNEL_ID)) {
			// 指定チャンネル以外のため終了
			return;
		}
		
		if (event.getName().equals("server") && event.getOption("cmd").getAsString().equals("start")) {
			// /server start : server起動の実行
			event.reply("server起動の実行").queue();
			ServerUtil.start();
		} else if (event.getName().equals("server") && event.getOption("cmd").getAsString().equals("stop")) {
			// /server stop : server停止の実行
			event.reply("server停止の実行").queue();
			ServerUtil.stop();
		} else if (event.getName().equals("server") && event.getOption("cmd").getAsString().equals("status")) {
			// /server status : serverの状態を確認
			event.reply("serverの状態を確認").queue();
			ServerUtil.status();
		} else if (event.getName().equals("list")) {
			// /list : サーバ内のplayerのリストを表示
			event.reply("サーバ内のplayerのリストを表示").queue();
			MinecraftUtil.list();
		} else if (event.getName().equals("sendmsg") && event.getOption("status").getAsString().equals("on")) {
			// /sendmsg on : メッセージの自動送信を有効
			if (ChatUserCheckService.setChatUser(event.getUser().getId(), event.getUser().getName())) {
				event.reply("メッセージの自動送信 有効").queue();
			} else {
				event.reply("処理に失敗").queue();
			}
		} else if (event.getName().equals("sendmsg") && event.getOption("status").getAsString().equals("off")) {
			// /sendmsg off : メッセージの自動送信を無効
			if (ChatUserCheckService.deleteChatUser(event.getUser().getId())) {
				event.reply("メッセージの自動送信 無効").queue();
			} else {
				event.reply("処理に失敗").queue();
			}
		} else if (event.getName().equals("say")) {
			// /say : ゲーム内チャットへメッセージを送信
			ChatMessage chatmessage = new ChatMessage();
			chatmessage.userName = event.getUser().getName();
			chatmessage.message = event.getOption("msg").getAsString();
			if (chatmessage.userName.isEmpty() || chatmessage.message.isEmpty()) {
				event.reply("メッセージの送信に失敗").queue();
			}
			ChatLogUtil.sendDiscordChat(chatmessage).getMessage();
			event.reply("[" + chatmessage.userName + "] " + chatmessage.message).queue();
		} else {
			event.reply("不明なコマンドです。").setEphemeral(true).queue();
		}
	}
}
