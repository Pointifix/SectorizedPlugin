package sectorized.constant;

import arc.Events;
import arc.func.Func;
import arc.struct.IntMap;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;

import java.util.HashMap;

public class MenuUtils {
    static IntMap<Func<Player, MenuContent>> menus = new IntMap<>();

    static HashMap<Player, MenuContent> lastContents = new HashMap<>();

    static {
        Events.on(EventType.MenuOptionChooseEvent.class, event -> {
            if (menus.containsKey(event.menuId)) {
                MenuContent current = lastContents.get(event.player);

                current.getAction(event.option).get(event.player);

                int nextMenuId = current.getLink(event.option);

                if (nextMenuId >= 0) {
                    Func<Player, MenuContent> nextMenu = menus.get(nextMenuId);
                    MenuContent next = nextMenu.get(event.player);

                    lastContents.put(event.player, next);
                    Call.menu(event.player.con(), nextMenuId, next.title, next.message, next.options);
                }
            }
        });
    }

    public static void addMenu(int menuId, Func<Player, MenuContent> contentFunc) {
        if (menus.containsKey(menuId)) {
            throw new IllegalStateException("Menu ID already exists!");
        }

        MenuUtils.menus.put(menuId, contentFunc);
    }

    public static void showMenu(int menuId, Player player) {
        if (!menus.containsKey(menuId)) {
            throw new IllegalStateException("Menu ID not found!");
        }

        Func<Player, MenuContent> menu = menus.get(menuId);
        MenuContent content = menu.get(player);

        lastContents.put(player, content);
        Call.menu(player.con(), menuId, content.title, content.message, content.options);
    }

    public static class MenuContent {
        public String title;
        public String message;
        public String[][] options;
        public int[][] links;
        public Handler[][] actions;

        public MenuContent(String title, String message, String[][] options, int[][] links, Handler[][] actions) {
            this.title = title;
            this.message = message;
            this.options = options;
            this.links = links;
            this.actions = actions;
        }

        public int getLink(int option) {
            int o = 0;
            for (int i = 0; i < this.links.length; i++) {
                for (int j = 0; j < this.links[i].length; j++) {
                    if (o == option) return this.links[i][j];

                    o++;
                }
            }

            return -1;
        }

        public Handler getAction(int option) {
            int o = 0;
            for (int i = 0; i < this.actions.length; i++) {
                for (int j = 0; j < this.actions[i].length; j++) {
                    if (o == option) return this.actions[i][j];

                    o++;
                }
            }

            return null;
        }
    }

    public interface Handler {
        void get(Player player);
    }
}
