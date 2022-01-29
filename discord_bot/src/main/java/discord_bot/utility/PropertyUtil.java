package discord_bot.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import discord_bot.common.Constants;

public class PropertyUtil {
	
	public static String getPropertyValue (String key) {

		Properties properties = new Properties();

		//プロパティファイルのパスを指定する
		String prppertyFilePath = Constants.PROPERTY_FILE_PATH;
		InputStream istream = null;
		try {
			istream = new FileInputStream(prppertyFilePath);
			properties.load(istream);
			return properties.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (istream != null) {
				try {
					istream.close();
				}
				catch (IOException e) {
				}
			}
		}
		return null;
	}
}
