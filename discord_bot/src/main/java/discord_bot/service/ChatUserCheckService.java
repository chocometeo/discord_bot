package discord_bot.service;

import java.util.ArrayList;
import java.util.List;

import discord_bot.common.ChatUser;
import discord_bot.common.CommandResult;

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
	public static CommandResult setChatUser(String userId, String userName) {
		
		
		
		if (isChatUser(userId)) {
			// 既に存在
			System.out.println("ユーザ登録済み");
			return new CommandResult(true, 1, "");
		}

		ChatUser chatUser = new ChatUser(userId, userName);
		chatUser.startChatUser();
		int count = 0;
		while (count < 10) {
			if (chatUser.getStatus()) {
				System.out.println("ユーザ登録に成功");
				userList.add(chatUser);
				return new CommandResult(true, 0, "");
			} else {
				count++;
			}
			// スレッドスリープ
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("ユーザ登録に失敗");
		return new CommandResult(false, 2, "");
	}
	
	/*
	 *  ユーザ登録の解除
	 */
	public static CommandResult deleteChatUser(String userId) {

		ChatUser chatuser = getChatUser(userId);
		
		if (chatuser != null) {
			if (chatuser.getStatus()) {
				// 有効ユーザを無効にする
				if (chatuser.stopChatUser()) {
					// リストから削除する
					userList.remove(chatuser);
					return new CommandResult(true, 0, "");
				}
			} else {
				// 指定時間経過のためリストから削除する
				userList.remove(chatuser);
				return new CommandResult(true, 0, "");
			}
		} else {
			return new CommandResult(true, 1, "指定されたユーザが見つかりません");
		}
		return new CommandResult(false, 2, "");
	}
}
