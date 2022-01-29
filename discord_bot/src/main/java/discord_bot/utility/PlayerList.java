package discord_bot.utility;

import java.util.ArrayList;
import java.util.List;

public class PlayerList {
	
	/*
	 *  取得した文字列からプレイヤーを抽出する
	 *  
	 *  @param str プレイヤーリストが含まれる文字列
	 *   ex)[23:40:46] [Server thread/INFO]: There are 2 of a max of 20 players online: cookie_nao27, shula
	 *  @return List<String> プレイヤーのリスト
	 */
	public static List<String> getPlayerList(String str) {
		
		List<String> playerList = new ArrayList<String>();
		
		int beginIndex = str.indexOf("players online:");
		
		if (beginIndex == -1) {
			// プレイヤーリストが含まれていない文字列
			return null;
		} else if (str.substring(beginIndex + 15).trim().length() <= 0) {
			// プレイヤーリストがいない場合
			return playerList;
		}
		
		for (String s: str.substring(beginIndex + 15).split(",")) {
			playerList.add(s.trim());			
		}
		
		return playerList;
	}
	
	

}
