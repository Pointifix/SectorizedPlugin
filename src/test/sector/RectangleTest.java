/*package sector;

import main.sectorized.sector.SubRectangle;
import org.junit.jupiter.api.Test;
import main.sectorized.sector.Rectangle;

public class RectangleTest {
    @Test
    public void intersecting() {
        Rectangle rectangle1 = new Rectangle(1, 3, 5, 7, 0, 0);
        Rectangle rectangle2 = new Rectangle(3, 1, 7, 5, 1, 1);

        SubRectangle intersection = rectangle1.intersect(rectangle2);

        System.out.println(intersection);

        assert intersection != null;
    }

    @Test
    public void notIntersecting() {
        Rectangle rectangle1 = new Rectangle(1, 1, 3, 3, 0, 0);
        Rectangle rectangle2 = new Rectangle(2, 4, 6, 8, 1, 1);

        SubRectangle intersection = rectangle1.intersect(rectangle2);

        System.out.println(intersection);

        assert intersection == null;
    }

    @Test
    public void borderIntersecting() {
        Rectangle rectangle1 = new Rectangle(1, 1, 3, 3, 0, 0);
        Rectangle rectangle2 = new Rectangle(3, 3, 6, 6, 1, 1);

        SubRectangle intersection = rectangle1.intersect(rectangle2);

        System.out.println(intersection);

        assert intersection != null;
    }

    @Test
    public void iterate() {
        Rectangle rectangle = new Rectangle(1, 1, 4, 4, 0, 0);

        int[] values = new int[3];

        rectangle.iterate((x, y, borderX, borderY) -> {
            if (borderX || borderY) {
                if (borderX) values[1]++;
                if (borderY) values[2]++;
            } else {
                values[0]++;
            }
        });

        for (int i = 0; i < 3; i++) {
            System.out.println(values[i]);
        }

        assert values[0] == 4 && values[1] == 8 && values[2] == 8;
    }
}
*/