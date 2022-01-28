package discord_bot;

import discord_bot.common.ContactBot;
import discord_bot.service.LogCheckService;
import discord_bot.service.MessageReceivedService;
import discord_bot.service.SlashCommandService;

public class App {
	public static void main(String[] args) {

		try {
			// Discordに接続、ログイン
			ContactBot.contactBot();

			// リスナーイベントの起動
			MessageReceivedService MRS = new MessageReceivedService();
			MRS.start();

			// スラッシュコマンドリスナーサービス
			SlashCommandService SCS = new SlashCommandService();
			SCS.start();
			
			// マインクラフトログを取得
			LogCheckService.startLogCheck();

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
