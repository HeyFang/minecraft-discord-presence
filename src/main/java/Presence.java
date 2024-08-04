package presence;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public final class Presence extends JavaPlugin {

    private JDA jda;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Initialize JDA with retry mechanism
        initializeJDA();

        // Add shutdown hook to ensure proper shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (jda != null) {
                jda.shutdown();
            }
        }));
    }

    private void initializeJDA() {
        int retries = 3;
        while (retries > 0) {
            try {
                jda = JDABuilder.createDefault("YOUR_DISCORD_BOT_TOKEN")
                        .enableIntents(GatewayIntent.GUILD_MESSAGES)
                        .build();
                jda.awaitReady();
                break;
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
                retries--;
                if (retries == 0) {
                    getLogger().severe("Failed to initialize JDA after multiple attempts.");
                } else {
                    getLogger().warning("Retrying JDA initialization...");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (jda != null) {
            jda.shutdown();
        }
    }

    public void setDiscordRichPresence(String playerName) {
        if (jda != null) {
            jda.getPresence().setActivity(Activity.playing("Player " + playerName + " joined the server"));
        }
    }
}