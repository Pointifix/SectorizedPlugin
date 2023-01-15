package sectorized.faction.core;

import mindustry.gen.Player;

public class Member {
    public Player player;
    public Faction faction;
    public boolean online = false;
    public MemberState state = MemberState.WAITING;

    public int wins = 0, losses = 0, score = 0, rank = -1;
    public float ratio = 0;
    public String discordTag;

    public Member(Player player) {
        this.player = player;
    }

    public enum MemberState {
        ALIVE,
        ELIMINATED,
        WAITING
    }
}
