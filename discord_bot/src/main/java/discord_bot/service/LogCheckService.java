package discord_bot.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import discord_bot.common.Constants;
import discord_bot.utility.ChatLogUtil;

public class LogCheckService extends Thread {
	
	// ログ監視スレッド制御
	private static LogCheckService info;
	private static boolean isRunning;
	private static boolean isStop;
	
	// logファイル監視用
	private static long pointer;
	private static long nextReturn;
	
	// logファイル監視対象制御フラグ
	private static boolean isLoginCheck;
	private static boolean isChatLogCheck;
	
	// Discordチャットをゲーム内転送制御フラグ
	private static boolean isSendDiscordChat;
	
	
	public LogCheckService () {
		isRunning = true;
		isStop = false;
		
		pointer = 0;
		nextReturn = 0;
		
		// 各種機能、初期値ON
		isLoginCheck = true;
		isChatLogCheck = true;
		isSendDiscordChat = true;
		
		
	}
	
	/*
	 * start()で実行されるメソッド
	 */
	public void run() {
		// スレッドの状態を更新
		isRunning = true;
		
		//読み込みたいログファイル
		String logFilePath = Constants.LOGFILE_PATH;
		//Charset
		Charset charset = StandardCharsets.UTF_8;
		RandomAccessFile reader = null;
        String line;
        byte[] bytes;
        
        //読み込んだ前回位置
        pointer = 0;
        //読み込んだ最終位置
        nextReturn = 0;


		while (!isStop) {
			
			// ログファイル読み取りループ
			try {
				// 前回の最終位置から読み込む
				reader = new RandomAccessFile(new File(logFilePath), "r");
				
				// 初回のみポインタを末尾に設定する
				if (pointer == 0) {
					pointer = reader.length();
				}
				// ポインタがファイルサイズよりも大きい場合、再設定
				if (pointer > reader.length()) {
					pointer = reader.length();
				}
				
				reader.seek(pointer);
				while (true) {
					line = reader.readLine();
					//読み込んだ位置を記録する
					nextReturn = reader.getFilePointer();
					if (line == null) {
						break;
					}

					//前回読み込んだ位置に戻り、今回の読み取り範囲をbyte配列で取得する(任意の文字コードで文字列に変換するため)
					{
						reader.seek(pointer);
						bytes = new byte[(int) (nextReturn - pointer)];
						reader.read(bytes);
						pointer = reader.getFilePointer();
						line = new String(bytes, charset);
						//末尾の改行コードを除去
						while (line.endsWith("\r") || line.endsWith("\n")) {
							line = line.substring(0, line.length() - 1);
						}
					}

					// ログイン・ログアウトチェック処理
					if (isLoginCheck) {
						ChatLogUtil.sendLogInMessage(line);
					}
					// チャットログチェック処理
					if (isChatLogCheck) {
						ChatLogUtil.sendInGameChat(line);
					}
					
				}
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				// File
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				// RandomAccessFile
				e.printStackTrace();
			}
			finally {
				if (reader != null) {
					try {
						reader.close();
					}
					catch (IOException e) {
					}
				}
			}
			
			// スレッドスリープ
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		
		isRunning = false;
		
	}
	
	/*
	 *  ログ取得スレッドの状態を返却
	 *  @return boolean
	 *  	true:ログ取得中
	 *  	false:停止中
	 */
	public static boolean getStatus() {
		return isRunning;
	}
	
	/*
	 *  マイクラログインユーザの通知を設定する
	 *  @param boolean
	 *  	true:ログインユーザの通知を行う
	 *  	false:ログインユーザの通知を行わない
	 */
	public static void setLoginCheckFlag(boolean flg) {
		isLoginCheck = flg;
		return;
	}
	
	/*
	 *  マイクラゲーム内チャットのDiscord転送の設定
	 *  @param boolean
	 *  	true:ゲーム内チャットの転送を行う
	 *  	false:ゲーム内チャットの転送を行わない  
	 */
	public static void setChatLogCheckFlag(boolean flg) {
		isChatLogCheck = flg;
		return;
	}
	
	/*
	 *  Discordチャットのマインクラフトゲーム内チャット転送の状態を取得・設定
	 *  @get
	 *  	@return boolean
	 *  		true:Discordチャットの転送を行う
	 *  		false:Discordチャットの転送を行わない
	 *  @set
	 *  	@param boolean
	 *  		true:Discordチャットの転送を行う
	 *  		false:Discordチャットの転送を行わない
	 */
	public static boolean getSendDiscordChatFlag() {
		return isSendDiscordChat;
	}
	public static void setSendDiscordChatFlag(boolean flg) {
		isSendDiscordChat = flg;
		return;
	}
	
	/*
	 *  ログ取得スレッドの開始
	 *   @return boolean 処理結果
	 */
	public static boolean startLogCheck() {
		
		try {
			if (!isRunning) {
				System.out.println("スレッドを開始します");
				info = new LogCheckService();
				info.start();
			} else {
				System.out.println("スレッドは起動しています");
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		
		return isRunning;
	}
	
	/*
	 *  ログ取得スレッドの停止
	 *  @return boolean 処理結果
	 */
	public static boolean stopLogCheck() {
		if (isRunning) {
			System.out.println("スレッドは停止しています");
			return true;
		}
		
		// ログ監視が起動している場合
		isStop = true;
		
		try {
			info.join();
			System.out.println("停止に成功");
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
		
		return !isRunning;
	}

	
	
}
