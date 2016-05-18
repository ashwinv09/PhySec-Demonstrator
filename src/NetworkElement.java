import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by ashwinv on 17.05.16 at 14:56.
 */
public class NetworkElement {
    Point centerPoint;
    NetworkElementType elementType;
    static BufferedImage imgBaseStation, imgClient;
    boolean ifSelected = false;
    Rectangle focusRect = new Rectangle();

    public NetworkElement(Point centerPoint, NetworkElementType elementType) {
        this.centerPoint = centerPoint;
        this.elementType = elementType;

        setFocusRect(focusRect);

        ClassLoader classLoader;
        InputStream inputStreamIcon;

        try {
            classLoader = Thread.currentThread().getContextClassLoader();

            inputStreamIcon = classLoader.getResourceAsStream("baseStation.png");
            imgBaseStation = ImageIO.read(inputStreamIcon);

            inputStreamIcon = classLoader.getResourceAsStream("clientNode.png");
            imgClient = ImageIO.read(inputStreamIcon);
        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    void setFocusRect(Rectangle focusRect) {
        focusRect.setBounds(centerPoint.x - 32, centerPoint.y - 32, 64, 64);   // image size (hard-coded to 64x64 pixels)
    }

    public void draw(Graphics graphics) {
        if (elementType == NetworkElementType.BASE_STATION_ELEMENT)
            graphics.drawImage(imgBaseStation, focusRect.x, focusRect.y, null);
        else if (elementType == NetworkElementType.CLIENT_ELEMENT)
            graphics.drawImage(imgClient, focusRect.x, focusRect.y, null);

        if (ifSelected) {
            graphics.setColor(new Color(0x0000FF));
            graphics.drawRect(focusRect.x, focusRect.y, 64, 64);
        }
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public boolean contains(Point centerPoint) {
        return focusRect.contains(centerPoint);
    }

    public boolean isSelected() {
        return ifSelected;
    }

    public void setSelected(boolean b) {
        ifSelected = b;
    }

    public static void getSelectedElements (List<NetworkElement> elements, List<NetworkElement> selectedElements) {
        selectedElements.clear();

        for (NetworkElement element : elements) {
            if (element.isSelected())
                selectedElements.add(element);
        }
    }

    public static void unselectAllElements(List<NetworkElement> elements) {
        for (NetworkElement element : elements)
            element.setSelected(false);
    }

    public static boolean selectOneElement(List<NetworkElement> elements, Point centerPoint) {
        for (NetworkElement element : elements) {
            if (element.contains(centerPoint)) {
                if (!element.isSelected()) {
                    NetworkElement.unselectAllElements(elements);
                    element.setSelected(true);
                }
                return true;
            }
        }
        return false;
    }

    public static void selectFocusRect(List<NetworkElement> elements, Rectangle focusRect) {
        for (NetworkElement element : elements)
            element.setSelected(focusRect.contains(element.centerPoint));
    }

    public static void changeSelectStatus(List<NetworkElement> elements, Point centerPoint) {
        for (NetworkElement element : elements)
            if (element.contains(centerPoint))
                element.setSelected(!element.isSelected());
    }

    public static void changePosition(List<NetworkElement> elements, Point newPoint) {
        for (NetworkElement element : elements) {
            if (element.isSelected()) {
                element.centerPoint.x += newPoint.x;
                element.centerPoint.y += newPoint.y;
                element.setFocusRect(element.focusRect);
            }
        }
    }

    public static NetworkElement getElementFromPoint(List<NetworkElement> elements, Point point) {
        for (NetworkElement element : elements) {
            if (element.contains(point))
                return element;
        }
        return null;
    }
}