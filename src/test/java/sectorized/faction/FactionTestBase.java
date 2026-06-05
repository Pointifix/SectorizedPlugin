package sectorized.faction;

import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.gen.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import sectorized.constant.Config;
import sectorized.constant.State;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;
import sectorized.faction.logic.FactionLogic;
import sectorized.faction.logic.MemberLogic;
import sectorized.faction.persistence.RankingPersistence;

import static org.mockito.Mockito.*;

public class FactionTestBase {

    @BeforeAll
    public static void setupConfig() {
        Config.reset();
    }

    @BeforeEach
    public void resetState() {
        State.gameState = State.GameState.ACTIVE;
        State.time = 0;
        State.winner = null;
    }

    protected RankingPersistence createPersistence() {
        return new RankingPersistence();
    }

    protected Player createMockPlayer(String name, String uuid) {
        Player player = mock(Player.class);
        when(player.uuid()).thenReturn(uuid);
        when(player.team()).thenReturn(Team.derelict);
        doNothing().when(player).sendMessage(anyString());
        return player;
    }

    protected Member createMember(Player player) {
        return new Member(player);
    }

    protected FactionLogic createFactionLogic(RankingPersistence persistence) {
        return new FactionLogic(persistence);
    }

    protected MemberLogic createMemberLogic(RankingPersistence persistence) {
        return new MemberLogic(persistence);
    }

    protected Faction createFaction(Member leader, Team team) {
        Faction faction = new Faction(team, State.time);
        faction.addMember(leader);
        faction.maxCores = 1;
        return faction;
    }
}
