package sectorized.faction;

import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.gen.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;
import sectorized.faction.logic.FactionLogic;
import sectorized.faction.logic.MemberLogic;
import sectorized.faction.persistence.RankingPersistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FactionLogicTest extends FactionTestBase {

    private RankingPersistence persistence;
    private FactionLogic factionLogic;
    private MemberLogic memberLogic;

    @BeforeEach
    void setUp() {
        persistence = createPersistence();
        factionLogic = createFactionLogic(persistence);
        memberLogic = createMemberLogic(persistence);
    }

    @Test
    void getNewFaction_createsFactionWithUniqueTeam() {
        Faction faction = factionLogic.getNewFaction();

        assertNotNull(faction);
        assertNotNull(faction.team);
        assertNotEquals(Team.derelict, faction.team);
        assertNotEquals(Team.sharded, faction.team);
        assertNotEquals(Team.crux, faction.team);
        assertNotEquals(Team.malis, faction.team);
    }

    @Test
    void getFaction_returnsCorrectFaction() {
        Faction faction = factionLogic.getNewFaction();

        Faction found = factionLogic.getFaction(faction.team);

        assertSame(faction, found);
    }

    @Test
    void getFaction_returnsNullForUnknownTeam() {
        Faction found = factionLogic.getFaction(Team.derelict);

        assertNull(found);
    }

    @Test
    void addToFaction_addsMemberAndSetsStateAndTeam() {
        Faction faction = factionLogic.getNewFaction();
        Player player = createMockPlayer("Requester", "uuid-req");
        Member member = createMember(player);

        factionLogic.addToFaction(faction, member);

        assertTrue(faction.members.contains(member));
        assertSame(faction, member.faction);
        assertEquals(Member.MemberState.ALIVE, member.state);
        verify(member.player).team(faction.team);
    }

    @Test
    void changeFaction_transfersMemberBetweenFactions() {
        Faction oldFaction = factionLogic.getNewFaction();
        Player player = createMockPlayer("Player", "uuid-player");
        Member member = createMember(player);
        oldFaction.addMember(member);

        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leader = createMember(leaderPlayer);
        Faction newFaction = factionLogic.getNewFaction();
        newFaction.addMember(leader);

        factionLogic.changeFaction(oldFaction, newFaction, member);

        assertFalse(oldFaction.members.contains(member));
        assertTrue(newFaction.members.contains(member));
        assertSame(newFaction, member.faction);
        assertEquals(Member.MemberState.ALIVE, member.state);
        verify(member.player).team(newFaction.team);
    }

    @Test
    void getJoinableFactionLeaders_excludesRequestersFaction() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leader = createMember(leaderPlayer);
        Faction faction = factionLogic.getNewFaction();
        faction.addMember(leader);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requester = createMember(requesterPlayer);

        factionLogic.addToFaction(faction, requester);

        Seq<Member> leaders = factionLogic.getJoinableFactionLeaders(requester);

        assertFalse(leaders.contains(leader));
    }

    @Test
    void getJoinableFactionLeaders_returnsOtherFactionLeaders() {
        Player leaderPlayer1 = createMockPlayer("Leader1", "uuid-leader1");
        Member leader1 = createMember(leaderPlayer1);
        Faction faction1 = factionLogic.getNewFaction();
        faction1.addMember(leader1);

        Player leaderPlayer2 = createMockPlayer("Leader2", "uuid-leader2");
        Member leader2 = createMember(leaderPlayer2);
        Faction faction2 = factionLogic.getNewFaction();
        faction2.addMember(leader2);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requester = createMember(requesterPlayer);

        factionLogic.addToFaction(faction2, requester);

        Seq<Member> leaders = factionLogic.getJoinableFactionLeaders(requester);

        assertEquals(1, leaders.size);
        assertSame(leader1, leaders.first());
    }

    @Test
    void getJoinableFactionLeaders_returnsEmptyWhenNoOtherFactions() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leader = createMember(leaderPlayer);
        Faction faction = factionLogic.getNewFaction();
        faction.addMember(leader);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requester = createMember(requesterPlayer);

        factionLogic.addToFaction(faction, requester);

        Seq<Member> leaders = factionLogic.getJoinableFactionLeaders(requester);

        assertTrue(leaders.isEmpty());
    }

    @Test
    void multipleGetNewFaction_returnsDifferentTeams() {
        Faction faction1 = factionLogic.getNewFaction();
        Faction faction2 = factionLogic.getNewFaction();
        Faction faction3 = factionLogic.getNewFaction();

        assertNotNull(faction1);
        assertNotNull(faction2);
        assertNotNull(faction3);
        assertNotEquals(faction1.team, faction2.team);
        assertNotEquals(faction2.team, faction3.team);
    }
}
