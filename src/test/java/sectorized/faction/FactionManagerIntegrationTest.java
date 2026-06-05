package sectorized.faction;

import arc.struct.Seq;
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

class FactionManagerIntegrationTest extends FactionTestBase {

    private RankingPersistence persistence;
    private FactionLogic factionLogic;
    private MemberLogic memberLogic;
    private FactionManager factionManager;

    @BeforeEach
    void setUp() {
        resetState();
        persistence = createPersistence();
        factionLogic = createFactionLogic(persistence);
        memberLogic = createMemberLogic(persistence);
        factionManager = new FactionManager();
        factionManager.initForTesting(factionLogic, memberLogic);
    }

    @Test
    void fullJoinFlow_playerJoinsFaction() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leaderMember = memberLogic.playerJoin(leaderPlayer);
        Faction faction = factionLogic.getNewFaction();
        faction.addMember(leaderMember);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requesterMember = memberLogic.playerJoin(requesterPlayer);

        factionManager.requestJoin(requesterMember, leaderMember);

        assertFalse(factionManager.getJoinRequests().isEmpty());
        assertEquals(1, factionManager.getJoinRequests().size);

        factionManager.handleAccept(leaderPlayer);

        assertTrue(factionManager.getJoinRequests().isEmpty());
        assertSame(faction, requesterMember.faction);
        assertEquals(Member.MemberState.ALIVE, requesterMember.state);
        verify(requesterMember.player).team(faction.team);
    }

    @Test
    void joinRequest_canBeDenied() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leaderMember = memberLogic.playerJoin(leaderPlayer);
        Faction faction = factionLogic.getNewFaction();
        faction.addMember(leaderMember);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requesterMember = memberLogic.playerJoin(requesterPlayer);

        factionManager.requestJoin(requesterMember, leaderMember);

        assertEquals(1, factionManager.getJoinRequests().size);

        factionManager.handleDeny(leaderPlayer);

        assertTrue(factionManager.getJoinRequests().isEmpty());
        assertNull(requesterMember.faction);
        assertEquals(Member.MemberState.WAITING, requesterMember.state);
    }

    @Test
    void doubleJoinRequest_showsWarning() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leaderMember = memberLogic.playerJoin(leaderPlayer);
        Faction faction = factionLogic.getNewFaction();
        faction.addMember(leaderMember);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requesterMember = memberLogic.playerJoin(requesterPlayer);

        factionManager.requestJoin(requesterMember, leaderMember);
        factionManager.requestJoin(requesterMember, leaderMember);

        assertEquals(1, factionManager.getJoinRequests().size);
    }

    @Test
    void playerAlreadyInFaction_canSwitchToAnother() {
        Player oldLeaderPlayer = createMockPlayer("OldLeader", "uuid-old");
        Member oldLeaderMember = memberLogic.playerJoin(oldLeaderPlayer);
        Faction oldFaction = factionLogic.getNewFaction();
        oldFaction.addMember(oldLeaderMember);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requesterMember = memberLogic.playerJoin(requesterPlayer);
        Faction requesterFaction = factionLogic.getNewFaction();
        requesterFaction.addMember(requesterMember);

        Player newLeaderPlayer = createMockPlayer("NewLeader", "uuid-new");
        Member newLeaderMember = memberLogic.playerJoin(newLeaderPlayer);
        Faction newFaction = factionLogic.getNewFaction();
        newFaction.addMember(newLeaderMember);

        factionManager.requestJoin(requesterMember, newLeaderMember);
        factionManager.handleAccept(newLeaderPlayer);

        assertTrue(factionManager.getJoinRequests().isEmpty());
        assertSame(newFaction, requesterMember.faction);
        assertEquals(Member.MemberState.ALIVE, requesterMember.state);
        verify(requesterMember.player).team(newFaction.team);
        assertFalse(requesterFaction.members.contains(requesterMember));
    }

    @Test
    void leaderCannotJoinOwnFaction() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leaderMember = memberLogic.playerJoin(leaderPlayer);
        Faction faction = factionLogic.getNewFaction();
        faction.addMember(leaderMember);

        Seq<Member> leaders = factionLogic.getJoinableFactionLeaders(leaderMember);

        for (int i = 0; i < leaders.size; i++) {
            assertNotSame(leaderMember, leaders.get(i));
        }
    }

    @Test
    void acceptWithoutPendingRequest_showsMessage() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        memberLogic.playerJoin(leaderPlayer);

        factionManager.handleAccept(leaderPlayer);

        assertTrue(factionManager.getJoinRequests().isEmpty());
    }

    @Test
    void denyWithoutPendingRequest_showsMessage() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        memberLogic.playerJoin(leaderPlayer);

        factionManager.handleDeny(leaderPlayer);

        assertTrue(factionManager.getJoinRequests().isEmpty());
    }

    @Test
    void handleJoin_withPendingRequest_showsWarning() {
        Player leaderPlayer = createMockPlayer("Leader", "uuid-leader");
        Member leaderMember = memberLogic.playerJoin(leaderPlayer);
        Faction faction = factionLogic.getNewFaction();
        faction.addMember(leaderMember);

        Player requesterPlayer = createMockPlayer("Requester", "uuid-req");
        Member requesterMember = memberLogic.playerJoin(requesterPlayer);

        factionManager.requestJoin(requesterMember, leaderMember);
        factionManager.handleJoin(requesterPlayer);

        assertEquals(1, factionManager.getJoinRequests().size);
    }
}
