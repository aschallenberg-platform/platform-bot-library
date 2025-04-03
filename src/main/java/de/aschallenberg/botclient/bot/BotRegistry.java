package de.aschallenberg.botclient.bot;

public class BotRegistry {
    private static Class<? extends Bot> botClass;

    public static void setBotClass(Class<? extends Bot> botClass) {
        BotRegistry.botClass = botClass;
    }

    public static Bot instantiateBot() {
        try {
            return botClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate bot class", e);
        }
    }
}
