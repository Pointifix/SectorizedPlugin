/*package sector;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import main.sectorized.sector.Rectangle;
import main.sectorized.sector.SectorManager;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.world.blocks.storage.CoreBlock;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class SectorManagerTest {
    private SectorManager sectorManager;

    public SectorManagerTest() {

    }

    @Test
    public void sectorManagerTimeTest() {
        int sum = 0;
        int iterations = 20;
        for (int iteration = 0; iteration < iterations; iteration++) {
            this.sectorManager = new SectorManager(700, 700);

            Random rand = new Random(2022 + 302 * iteration);

            long start = System.currentTimeMillis();
            for (int teamId = 3; teamId < 53; teamId++) {
                int randX = rand.nextInt(sectorManager.width);
                int randY = rand.nextInt(sectorManager.height);

                while (this.sectorManager.sectors[randX][randY] != 0) {
                    randX = rand.nextInt(sectorManager.width);
                    randY = rand.nextInt(sectorManager.height);
                }

                for (int i = 0; i < rand.nextInt(200); i++) {
                    if (i == 0 ? this.sectorManager.sectors[randX][randY] == 0 : this.sectorManager.sectors[randX][randY] == teamId) {
                        this.sectorManager.addCore(randX, randY, (CoreBlock) Blocks.coreShard, teamId);
                    }

                    randX = Mathf.clamp(randX + (int) Math.pow(-1, rand.nextInt(2)) * (rand.nextInt(9) + 10), 0, sectorManager.width - 1);
                    randY = Mathf.clamp(randY + (int) Math.pow(-1, rand.nextInt(2)) * (rand.nextInt(9) + 10), 0, sectorManager.height - 1);
                }
            }
            long finish = System.currentTimeMillis();
            sum += (finish - start);
        }

        System.out.println("Time elapsed average: " + (sum / iterations));

        printSectors();
    }

    @Test
    public void sectorManagerTest() {
        this.sectorManager = new SectorManager(700, 700);

        Random rand = new Random(2022);
        for (int teamId = 3; teamId < 53; teamId++) {
            int randX = rand.nextInt(sectorManager.width);
            int randY = rand.nextInt(sectorManager.height);

            while (this.sectorManager.sectors[randX][randY] != 0) {
                randX = rand.nextInt(sectorManager.width);
                randY = rand.nextInt(sectorManager.height);
            }

            for (int i = 0; i < rand.nextInt(200); i++) {
                if (i == 0 ? this.sectorManager.sectors[randX][randY] == 0 : this.sectorManager.sectors[randX][randY] == teamId) {
                    this.sectorManager.addCore(randX, randY, (CoreBlock) Blocks.coreShard, teamId);
                }

                randX = Mathf.clamp(randX + (int) Math.pow(-1, rand.nextInt(2)) * (rand.nextInt(9) + 10), 0, sectorManager.width - 1);
                randY = Mathf.clamp(randY + (int) Math.pow(-1, rand.nextInt(2)) * (rand.nextInt(9) + 10), 0, sectorManager.height - 1);
            }
        }

        printSectors();
    }

    @Test
    public void sectorManagerTestRemove() {
        this.sectorManager = new SectorManager(700, 700);

        Random rand = new Random(2022);
        for (int teamId = 3; teamId < 53; teamId++) {
            int randX = rand.nextInt(sectorManager.width);
            int randY = rand.nextInt(sectorManager.height);

            while (this.sectorManager.sectors[randX][randY] != 0) {
                randX = rand.nextInt(sectorManager.width);
                randY = rand.nextInt(sectorManager.height);
            }

            for (int i = 0; i < rand.nextInt(50); i++) {
                if (i == 0 ? this.sectorManager.sectors[randX][randY] == 0 : this.sectorManager.sectors[randX][randY] == teamId) {
                    this.sectorManager.addCore(randX, randY, (CoreBlock) Blocks.coreShard, teamId);
                }

                if (rand.nextFloat() < 0.2 && this.sectorManager.coreBuildRectangleMap.size() > 0) {
                    ArrayList<Integer> list = new ArrayList<>();
                    this.sectorManager.coreBuildRectangleMap.keySet().iterator().forEachRemaining(list::add);
                    Point2 p = Point2.unpack(list.get(rand.nextInt(list.size())));

                    this.sectorManager.removeCore(p.x, p.y);
                }

                randX = Mathf.clamp(randX + (int) Math.pow(-1, rand.nextInt(2)) * (rand.nextInt(9) + 10), 0, sectorManager.width - 1);
                randY = Mathf.clamp(randY + (int) Math.pow(-1, rand.nextInt(2)) * (rand.nextInt(9) + 10), 0, sectorManager.height - 1);
            }
        }

        printSectors();
    }

    private void printSectors() {
        BufferedImage bufferedImage = new BufferedImage(this.sectorManager.width, this.sectorManager.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();

        for (int x = 0; x < this.sectorManager.width; x++) {
            for (int y = 0; y < this.sectorManager.height; y++) {
                if (this.sectorManager.sectors[x][y] > 0)
                    bufferedImage.setRGB(x, y, Team.get(this.sectorManager.sectors[x][y]).color.rgb888());
                if (this.sectorManager.sectors[x][y] < 0) bufferedImage.setRGB(x, y, Color.white.rgb888());
            }
        }

        for (Rectangle rectangle : this.sectorManager.gridAccelerator.getIntersectingRectangles(new Rectangle(0, 0, this.sectorManager.width, this.sectorManager.height, -1, -1))) {
            //graphics2D.drawString(String.valueOf(rectangle.zIndex), rectangle.minX, rectangle.maxY);
        }

        System.out.println("debug");
    }
}
*/