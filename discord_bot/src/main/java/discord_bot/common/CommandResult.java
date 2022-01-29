package discord_bot.common;

public class CommandResult {
	
	// コマンド実施結果
	public boolean result;
	
	// コマンド実施結果コード
	public int code;
	
	// コマンド実施結果メッセージ
	public String message;
	
	public CommandResult (boolean res, int cd, String msg) {
		result = res;
		code = cd;
		message = msg;
	}

}
