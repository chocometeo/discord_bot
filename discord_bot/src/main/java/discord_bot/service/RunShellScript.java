package discord_bot.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RunShellScript {

	// 実行コマンドのパス
	public String shellPath;
	// 実行結果の標準出力
	public String resultStr;
	// 実行結果のリターンコード
	public int returnCode;
	// 実行コマンドが正常に実行されたかどうか
	public boolean result;

	/*
	 * 引数のパスにあるシェル、または引数のコマンドを実行する
	 * 
	 * @param  s
	 */
	public void run(String s) {

		shellPath = s;
		result = exec(s);
		// returnCode = getReturnCode();
		return;
	}
	
	public void run(String[] s) {

		shellPath = s.toString();
		result = exec(s);
		// returnCode = getReturnCode();
		return;
	}

	public String getResultString() {
		String s = resultStr;
		if (resultStr.length() >= 500) {
			s = resultStr.substring(0, 500);
		}
		return s;
	}

	public boolean exec(String str) {
		return exec(new String[] {str});
	}

	public boolean exec(String str[]) {

		boolean result = false;

		try {
			// 実行準備
			ProcessBuilder pb = new ProcessBuilder(str);
			// 標準エラーの、標準出力へのマージ
			pb.redirectErrorStream(true);

			// 実行
			Process process = pb.start();
			// 終了待ち
			process.waitFor();
			// rturnCode
			returnCode = process.exitValue();

			// Read output
			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line);
			}

			System.out.println(output.toString());
			resultStr = output.toString();
			result = true;

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(shellPath);
			System.out.println("実行に失敗");
			resultStr = "実行に失敗";
			returnCode = -1;
		}
		return result;
	}
}