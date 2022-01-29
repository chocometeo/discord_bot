package testwork;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import java.util.EnumSet;

import javax.security.auth.login.LoginException;

/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Copyright 2015 Austin Keener、Michael Ritter、FlorianSpieß、およびJDA寄稿者
 *
 */

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashBotExample extends ListenerAdapter
{
    public static void main(String[] args) throws LoginException
    {
        JDA jda = JDABuilder.createLight("BOT_TOKEN_HERE", EnumSet.noneOf(GatewayIntent.class)) // スラッシュコマンドはインテントを必要としません slash commands don't need any intents
                .addEventListeners(new SlashBotExample())
                .build();

        // これらのコマンドは、作成/更新/削除後にアクティブ化されるまでに最大1時間かかります
        // These commands take up to an hour to be activated after creation/update/delete
        CommandListUpdateAction commands = jda.updateCommands();

        // 必要なオプションを備えたモデレートコマンド
        // Moderation commands with required options
        commands.addCommands(
            new CommandData("ban", "Ban a user from this server. Requires permission to ban users.")
                .addOptions(new OptionData(USER, "user", "The user to ban") // USER type allows to include members of the server or other users by id
                    .setRequired(true)) // This command requires a parameter
                .addOptions(new OptionData(INTEGER, "del_days", "Delete messages from the past days.")) // This is optional
        );

        // 簡単な返信コマンド
        // Simple reply commands
        commands.addCommands(
            new CommandData("say", "Makes the bot say what you tell it to")
                .addOptions(new OptionData(STRING, "content", "What the bot should say")
                    .setRequired(true))
        );

        // 入力のないコマンド
        // Commands without any inputs
        commands.addCommands(
            new CommandData("leave", "Make the bot leave the server")
        );

        commands.addCommands(
            new CommandData("prune", "Prune messages from this channel")
                .addOptions(new OptionData(INTEGER, "amount", "How many messages to prune (Default 100)"))
        );

        // 新しいコマンドセットを不和に送信します。これにより、既存のグローバルコマンドが、ここで提供される新しいセットで上書きされます。
        // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
        commands.queue();
    }


    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName())
        {
        case "ban":
            Member member = event.getOption("user").getAsMember(); // 「ユーザー」オプションが必要なので、nullは必要ありません-ここをチェックしてください the "user" option is required so it doesn't need a null-check here
            User user = event.getOption("user").getAsUser();
            ban(event, user, member);
            break;
        case "say":
            say(event, event.getOption("content").getAsString()); // コンテンツが必要なのでnullはありません-ここをチェックしてください content is required so no null-check here
            break;
        case "leave":
            leave(event);
            break;
        case "prune": // ボタンプロンプト付きの2段階コマンド 2 stage command with a button prompt
            prune(event);
            break;
        default:
            event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event)
    {
    	// ユーザーはこのIDをスプーフィングする可能性があるため、これを使用する場合は注意が必要です。
        // users can spoof this id so be careful what you do with this
        String[] id = event.getComponentId().split(":"); // これは、ボタンで指定したカスタムIDです this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];
        // このように状態を保存する場合は、署名やローカルキャッシュなど、自分で生成したものであることを確認することを強くお勧めします。
        // When storing state like this is it is highly recommended to do some kind of verification that it was generated by you, for instance a signature or local cache
        if (!authorId.equals(event.getUser().getId()))
            return;
        event.deferEdit().queue(); // ボタンがクリックされたことを確認します。そうしないと、インタラクションが失敗します acknowledge the button was clicked, otherwise the interaction will fail
 
        MessageChannel channel = event.getChannel();
        switch (type)
        {
            case "prune":
                int amount = Integer.parseInt(id[2]);
                event.getChannel().getIterableHistory()
                    .skipTo(event.getMessageIdLong())
                    .takeAsync(amount)
                    .thenAccept(channel::purgeMessages);
                // フォールスルーボタンでプロンプトメッセージを削除する
                // fallthrough delete the prompt message with our buttons
            case "delete":
                event.getHook().deleteOriginal().queue();
        }
    }

    public void ban(SlashCommandEvent event, User user, Member member)
    {
        event.deferReply(true).queue(); // 他のことをする前に、コマンドを受け取ったことをユーザーに知らせてください Let the user know we received the command before doing anything else
        InteractionHook hook = event.getHook(); // これは、チャネルに権限がなくてもメッセージを送信できる特別なWebhookであり、一時的なメッセージも許可します This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages
        hook.setEphemeral(true); // ここにあるすべてのメッセージは、暗黙的に一時的なものになります All messages here will now be ephemeral implicitly
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS))
        {
            hook.sendMessage("You do not have the required permissions to ban users from this server.").queue();
            return;
        }

        Member selfMember = event.getGuild().getSelfMember();
        if (!selfMember.hasPermission(Permission.BAN_MEMBERS))
        {
            hook.sendMessage("I don't have the required permissions to ban users from this server.").queue();
            return;
        }

        if (member != null && !selfMember.canInteract(member))
        {
            hook.sendMessage("This user is too powerful for me to ban.").queue();
            return;
        }

        int delDays = 0;
        OptionMapping option = event.getOption("del_days");
        if (option != null) // null = not provided
            delDays = (int) Math.max(0, Math.min(7, option.getAsLong()));
        // ユーザーを禁止し、成功応答を送信します
        // Ban the user and send a success response
        event.getGuild().ban(user, delDays)
            .flatMap(v -> hook.sendMessage("Banned user " + user.getAsTag()))
            .queue();
    }

    public void say(SlashCommandEvent event, String content)
    {
        event.reply(content).queue(); // これには権限は必要ありません！ This requires no permissions!
    }

    public void leave(SlashCommandEvent event)
    {
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS))
            event.reply("You do not have permissions to kick me.").setEphemeral(true).queue();
        else
            event.reply("Leaving the server... :wave:") // うん、受け取った Yep we received it
                 .flatMap(v -> event.getGuild().leave()) // コマンドを確認した後、サーバーを離れます Leave server after acknowledging the command
                 .queue();
    }

    public void prune(SlashCommandEvent event)
    {
        OptionMapping amountOption = event.getOption("amount"); // これはオプションとして構成されているため、nullを確認してください This is configured to be optional so check for null
        int amount = amountOption == null
                ? 100 // default 100
                : (int) Math.min(200, Math.max(2, amountOption.getAsLong())); // 施行：2〜200の間でなければなりません enforcement: must be between 2-200
        String userId = event.getUser().getId();
        event.reply("This will delete " + amount + " messages.\nAre you sure?") // ボタンメニューでユーザーにプロンプトを表示する prompt the user with a button menu
            .addActionRow(// これは、「<style>（<id>、<label>）」を意味します。ユーザーはIDを偽装できるため、何らかの検証システムをセットアップします。 this means "<style>(<id>, <label>)" the id can be spoofed by the user so setup some kinda verification system
                Button.secondary(userId + ":delete", "Nevermind!"),
                Button.danger(userId + ":prune:" + amount, "Yes!")) // 最初のパラメータは、上記のonButtonClickで使用するコンポーネントIDです。 the first parameter is the component id we use in onButtonClick above
            .queue();
    }
}
