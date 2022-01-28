package discord_bot.common;

import java.util.Calendar;
import java.util.Date;

public class ChatUser extends Thread {
	
	// スレッド制御
	public boolean isRunning;
	public boolean isStop;
	
	// ユーザ情報
	public String userId;
	public String userName;
		
	public ChatUser (String uId, String uName) {
		isStop = false;
		userId = uId;
		userName = uName;
	}
	
	public void run() {
		System.out.println("ユーザ登録");
		// スレッドの状態を更新
		isRunning = true;
		
		// 処理開始時刻から閾値の取得
		Date setDate = new Date();
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(setDate);
		startCal.add(Calendar.MINUTE, 30);
		setDate = startCal.getTime();
		
		while (!isStop) {
			Date nowDate = new Date();
			
			// チャットユーザの有効時間の判定
			if (nowDate.after(setDate)) {
				// 
				isStop = true;
			}
			
			// スレッドスリープ
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				isRunning = false;
			}
		}
		System.out.println("ユーザ破棄");
		isRunning = false;
	}
	
	/*
	 *  チャットユーザの有効化
	 *   
	 */
	public ChatUser startChatUser() {
		// 指定ユーザの発言をマイクラ内に転送開始
		try {
			if (!isRunning) {
				System.out.println("チャットユーザの設定");
				this.start();
			} else {
				System.out.println("チャットユーザの設定済み");
				return this;
			}
		} catch (Exception e) {
			return null;
		}
		return this;
	}
	
	/*
	 *  チャットユーザの無効化
	 *  
	 */
	public boolean stopChatUser() {
		if (!isRunning) {
			System.out.println("チャットユーザの設定はされていません");
			return true;
		}
		
		// 起動している場合
		isStop = true;
		
		try {
			this.join();
			System.out.println("停止に成功");
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
		return !isRunning;
	}
	
	public boolean getStatus() {
		return this.isRunning;		
	}
	
	
}
