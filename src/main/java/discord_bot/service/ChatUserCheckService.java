package discord_bot.service;

import java.util.ArrayList;
import java.util.List;

import discord_bot.common.ChatUser;

public class ChatUserCheckService {
	
	// ユーザ情報格納リスト
	private static List<ChatUser> userList = new ArrayList<ChatUser>();
	
	/*
	 *  指定ユーザが有効かどうか
	 */
	public static boolean isChatUser(String userId) {
		for (ChatUser chatuser : userList) {
			if (userId.equals(chatuser.userId)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 *  指定ユーザのインスタンスを取得
	 */
	public static ChatUser getChatUser(String userId) {
		for (ChatUser chatuser : userList) {
			if (userId.equals(chatuser.userId)) {
				return chatuser;
			}
		}
		return null;
	}
	
	/*
	 *  新しいユーザを登録
	 */
	public static boolean setChatUser(String userId, String userName) {
		
		if (isChatUser(userId)) {
			// 既に存在
			System.out.println("ユーザ登録済み");
			return true;
		}

		ChatUser chatUser = new ChatUser(userId, userName);
		chatUser.startChatUser();
		int count = 0;
		while (count < 10) {
			if (chatUser.getStatus()) {
				System.out.println("ユーザ登録に成功");
				userList.add(chatUser);
				return true;
			} else {
				count++;
			}
			// スレッドスリープ
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("ユーザ登録に失敗");
		return false;
	}
	
	/*
	 *  ユーザ登録の解除
	 */
	public static boolean deleteChatUser(String userId) {

		ChatUser chatuser = getChatUser(userId);
		if (chatuser != null) {
			// 有効ユーザを無効にする
			if (chatuser.stopChatUser()) {
				// リストから削除する
				userList.remove(chatuser);
				return true;
			} else {
				return false;
			}
		} else {
			System.out.println("指定されたユーザが見つかりません");
			return false;
		}
	}
}
