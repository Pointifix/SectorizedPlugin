package sectorized;

import arc.util.CommandHandler;

public interface Manager {
    void init();

    void reset();

    void registerServerCommands(CommandHandler handler);

    void registerClientCommands(CommandHandler handler);
}
