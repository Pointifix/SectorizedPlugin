/*package sector;

import main.sectorized.sector.GridAccelerator;
import main.sectorized.sector.Rectangle;
import org.junit.jupiter.api.Test;

public class GridAcceleratorTest {
    private final GridAccelerator gridAccelerator;

    public GridAcceleratorTest() {
        this.gridAccelerator = new GridAccelerator(100, 100);
    }

    @Test
    public void getIntersectingRectanglesTest() {
        Rectangle test = new Rectangle(10, 10, 30, 30, 0, 0);

        this.gridAccelerator.addRectangle(test);
        this.gridAccelerator.addRectangle(new Rectangle(15, 10, 35, 30, 1, 1));
        this.gridAccelerator.addRectangle(new Rectangle(-10, -10, 10, 10, 2, 2));
        this.gridAccelerator.addRectangle(new Rectangle(50, 30, 70, 50, 3, 3));

        Rectangle[] intersectingRectangles = this.gridAccelerator.getIntersectingRectangles(test);

        for (Rectangle rect : intersectingRectangles) {
            System.out.println(rect);
        }

        System.out.println(intersectingRectangles.length);
    }
}
*/