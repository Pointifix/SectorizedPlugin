package sectorized;

import arc.math.geom.Point2;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import sectorized.faction.core.Faction;
import sectorized.faction.core.Member;
import sectorized.world.map.Biomes;

public class SectorizedEvents {
    static {
        // create objects to load class (prevents class not found when plugin is updated)
        new GamemodeStartEvent();
        new BiomesGeneratedEvent();
        new RestartEvent("");
        new ShutdownEvent();
        new NewMemberEvent(null);
        new CoreBuildEvent(null);
        new CoreDestroyEvent(null, null);
        new MemberSpawnedEvent(null, null);
        new NoSpawnPointAvailableEvent(null);
        new EliminateFactionEvent(null, null, false);
        new TeamDominatingEvent(null);
        new NoTeamDominatingEvent();
    }

    public static class GamemodeStartEvent {
        public GamemodeStartEvent() {

        }
    }

    public static class BiomesGeneratedEvent {
        public BiomesGeneratedEvent() {

        }
    }

    public static class RestartEvent {
        public final String reason;

        public RestartEvent(String reason) {
            this.reason = reason;
        }
    }

    public static class ShutdownEvent {
        public ShutdownEvent() {
        }
    }

    public static class NewMemberEvent {
        public final Member member;

        public NewMemberEvent(Member member) {
            this.member = member;
        }
    }

    public static class CoreBuildEvent {
        public final Tile tile;

        public CoreBuildEvent(Tile tile) {
            this.tile = tile;
        }
    }

    public static class CoreDestroyEvent {
        public final CoreBlock.CoreBuild coreBuild;
        public final Faction faction;

        public CoreDestroyEvent(CoreBlock.CoreBuild coreBuild, Faction faction) {
            this.coreBuild = coreBuild;
            this.faction = faction;
        }
    }

    public static class MemberSpawnedEvent {
        public final Point2 spawnPoint;
        public final Member member;

        public MemberSpawnedEvent(Point2 spawnPoint, Member member) {
            this.spawnPoint = spawnPoint;
            this.member = member;
        }
    }

    public static class NoSpawnPointAvailableEvent {
        public final Member member;

        public NoSpawnPointAvailableEvent(Member member) {
            this.member = member;
        }
    }

    public static class EliminateFactionEvent {
        public final Faction defender, attacker;
        public final boolean fallback;

        public EliminateFactionEvent(Faction defender, Faction attacker, boolean fallback) {
            this.defender = defender;
            this.attacker = attacker;
            this.fallback = fallback;
        }
    }

    public static class TeamDominatingEvent {
        public final Team team;

        public TeamDominatingEvent(Team team) {
            this.team = team;
        }
    }

    public static class NoTeamDominatingEvent {
        public NoTeamDominatingEvent() {
        }
    }

    public static class BiomeVoteFinishedEvent {
        Biomes.Biome biome;
        int votes;

        public BiomeVoteFinishedEvent(Biomes.Biome biome, int votes) {
            this.biome = biome;
            this.votes = votes;
        }
    }
}
