package discord_bot.common;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class ContactBot {

	public static JDABuilder jb;
	public static JDA jda;


	/**
	 * botをDiscordに接続する
	 * 
	 */
	public static void contactBot() throws LoginException, InterruptedException {

		try {
			// JDAオブジェクトの作成
			// トークン設定
			jb = JDABuilder.createDefault(Constants.TOKEN);
			
			// ステータス表示（MinecraftServerをプレイ中）
			jb.setActivity(Activity.playing("MinecraftServer"));
			
			jda = jb.build();
			
			jda.awaitReady();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
}
