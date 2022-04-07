package sectorized.faction.core;

import arc.struct.Seq;
import mindustry.game.Team;
import sectorized.constant.MessageUtils;

public class Faction {
    public final Team team;

    public int maxCores = 0;

    public final Seq<Member> members = new Seq<>();

    public final double time;

    public Faction(Team team, double time) {
        this.team = team;
        this.time = time;
    }

    public void addMember(Member member) {
        this.members.add(member);
        member.faction = this;
        member.state = Member.MemberState.ALIVE;
        member.player.team(team);
    }

    public void removeMember(Member member) {
        if (this.members.first() == member) {
            if (this.members.size > 1) {
                Member newLeader = this.members.get(1);

                MessageUtils.sendMessage(newLeader.player, "You are now the team leader because [gold]" + member.player.name + MessageUtils.defaultColor + " left the team!", MessageUtils.MessageLevel.INFO);
            }
        }

        this.members.remove(member);
        member.faction = null;
    }
}
