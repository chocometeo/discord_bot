package discord_bot.common;

import java.util.Calendar;
import java.util.Date;

import discord_bot.service.ChatUserCheckService;

public class ChatUser extends Thread {
	
	// スレッド制御
	public boolean isRunning;
	public boolean isStop;
	
	// ユーザ情報
	public String userId;
	public String userName;
	
	// 有効期限
	public Date setDate;
	
	
	public ChatUser (String uId, String uName) {
		isStop = false;
		userId = uId;
		userName = uName;
		// 処理開始時刻から閾値の取得
		upddateValidityPeriod();
	}
	
	public void run() {
		System.out.println("[" + userName + "]" + "ユーザ登録");
		// スレッドの状態を更新
		isRunning = true;
		
		while (!isStop) {
			Date nowDate = new Date();
			
			// チャットユーザの有効時間の判定
			if (nowDate.after(setDate)) {
				// 
				isStop = true;
				break;
			}
			
			// スレッドスリープ
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				isStop = true;
				break;
			}
		}
		isRunning = false;
		ChatUserCheckService.deleteChatUser(userId);
		System.out.println("[" + userName + "]" + "ユーザ登録削除");
		ContactBot.jda.getTextChannelById(Constants.COMMANDCHANNEL_ID).sendMessage("[" + userName + "]" + "チャット転送の終了").queue();
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
		interrupt();
		
		try {
			join();
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
	
	/*
	 *  ユーザの有効期間を延長する
	 *
	 */
	public Date upddateValidityPeriod() {
		setDate = new Date();
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(setDate);
		startCal.add(Calendar.MINUTE, 30);
		setDate = startCal.getTime();
		return setDate;
	}
	
}
