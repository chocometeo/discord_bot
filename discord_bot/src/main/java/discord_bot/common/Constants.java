package discord_bot.common;

import discord_bot.utility.PropertyUtil;

public class Constants {
	
	// prppertyFilePath
	public static final String PROPERTY_FILE_PATH = "./properties/discord_bot.properties";
	
	// choco-minecraft.bot
	public static final String TOKEN = PropertyUtil.getPropertyValue("TOKEN");
	
	// マイクラサーバ　サーバID
	public static final String GUILD_ID = PropertyUtil.getPropertyValue("GUILD_ID");

	// マイクラサーバ　チャンネルID
	public static final String COMMANDCHANNEL_ID = PropertyUtil.getPropertyValue("COMMANDCHANNEL_ID");
	// logs
	public static final String CHATCHANNEL_ID = PropertyUtil.getPropertyValue("CHATCHANNEL_ID");
	
	// サーバログファイルパス
	public static final String LOGFILE_PATH = PropertyUtil.getPropertyValue("LOGFILE_PATH");
}
