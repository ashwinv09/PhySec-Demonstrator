import java.awt.*;

/**
 * Created by ashwinv on 17.05.16 at 15:05.
 */
public class NetworkConnection {
    NetworkElement element1, element2;

    public NetworkConnection(NetworkElement element1, NetworkElement element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public void draw(Graphics graphics) {
        Point centerPoint1 = element1.getCenterPoint();
        Point centerPoint2 = element2.getCenterPoint();
        graphics.setColor(new Color(0xFF0000));
        graphics.drawLine(centerPoint1.x, centerPoint1.y, centerPoint2.x, centerPoint2.y);
    }
}