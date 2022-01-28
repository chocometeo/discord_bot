package testwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Command {

	public static String sendCommand(String command) {
		String str = "";
		try {
			Runtime runtime = Runtime.getRuntime();
			Process result = runtime.exec(command);

			StringBuilder output = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(result.getInputStream()));

			String line;
			while ((line = br.readLine()) != null) {
				output.append(line);
			}
			str = output.toString();
		} catch (Exception e) {
			System.out.println(command);
			System.out.println("コマンドの実行に失敗");
		}

		return str.substring(0, 500);
	}

}
