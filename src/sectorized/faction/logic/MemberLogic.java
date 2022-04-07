package sectorized.faction.logic;

import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.gen.Player;
import sectorized.faction.core.Member;
import sectorized.faction.persistence.RankingPersistence;

public class MemberLogic {
    private final RankingPersistence persistence;

    private final Seq<Member> members = new Seq<>();

    public MemberLogic(RankingPersistence persistence) {
        this.persistence = persistence;
    }

    public Member playerJoin(Player player) {
        Member member = members.find(m -> m.player.uuid().equals(player.uuid()));

        if (member == null) {
            member = addMember(player);
        } else {
            member.player = player;
        }

        persistence.getRanking(member);

        member.online = true;

        if (member.faction == null) {
            member.player.team(Team.derelict);
            member.player.unit().kill();
        } else {
            member.player.team(member.faction.team);
        }

        return member;
    }

    public Member playerLeave(Player player) {
        Member member = getMember(player);

        persistence.setRanking(member);
        member.online = false;

        return member;
    }

    public Member getMember(Player player) {
        Member member = members.find(m -> m.player.uuid().equals(player.uuid()));

        assert member != null;

        return member;
    }

    private Member addMember(Player player) {
        Member member = new Member(player);
        members.add(member);

        return member;
    }
}
