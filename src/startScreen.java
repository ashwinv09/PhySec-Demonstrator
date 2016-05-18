import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashwinv on 17.05.16 at 11:49.
 */
public class startScreen extends JComponent {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private List<NetworkElement> elements = new ArrayList<NetworkElement>();
//    private Set<NetworkElement> elementSet = new HashSet<NetworkElement>();
//    private List<NetworkElement> selectedElements = new ArrayList<NetworkElement>();
    private List<NetworkConnection> connections = new ArrayList<NetworkConnection>();
    private Point point = new Point(WIDTH/2, HEIGHT/2);
    private Rectangle focusRect = new Rectangle();
    private static boolean ifSelected = false;

    static List<BaseStationElement> listOfBaseStationElements = new ArrayList<BaseStationElement>();
    static BaseStationElement newBaseStationElement;

    static List<ClientElement> listOfClientElements = new ArrayList<ClientElement>();
    static ClientElement newClientElement;

    private boolean flagDragConnection = false;
    private Point dragInitialPoint, dragNewPoint;

    public startScreen() {
        this.setOpaque(true);

        NetworkElement.unselectAllElements(elements);
        Point centerPoint = point;
        NetworkElement element = new NetworkElement(centerPoint, NetworkElementType.BASE_STATION_ELEMENT);
        element.setSelected(true);
        elements.add(element);
        newBaseStationElement = createBaseStationElementInstance(element);
        drawScreen();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                point = mouseEvent.getPoint();

                if (mouseEvent.isControlDown()) {
                    //TODO
                }

                if (mouseEvent.isShiftDown())
                    NetworkElement.changeSelectStatus(elements, point);
                else if (NetworkElement.selectOneElement(elements, point))  //mouse press on some element
                    ifSelected = false;
                else {
                    NetworkElement.unselectAllElements(elements);   //mouse press on canvas
                    ifSelected = true;
                }

                mouseEvent.getComponent().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (flagDragConnection && !ifSelected && !mouseEvent.isControlDown()) {
                    NetworkElement dragFinal = NetworkElement.getElementFromPoint(elements, dragNewPoint);
                    if (dragFinal != null)      //create connection if drag destination is an element
                        connections.add(new NetworkConnection(NetworkElement.getElementFromPoint(elements, dragInitialPoint), dragFinal));
                }

                flagDragConnection = false;
                dragInitialPoint = null;
                dragNewPoint = null;

                ifSelected = false;
                focusRect.setBounds(0, 0, 0, 0);

                mouseEvent.getComponent().repaint();
            }

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                point = mouseEvent.getPoint();

                if (NetworkElement.getElementFromPoint(elements, point) == null) {      //create new client only if no existing element is occupying that area
                    NetworkElement.unselectAllElements(elements);
                    Point centerPoint = point.getLocation();
                    NetworkElement element = new NetworkElement(centerPoint, NetworkElementType.CLIENT_ELEMENT);
                    element.setSelected(true);
                    elements.add(element);
                    newClientElement = createScalingInstance(element);
                    drawScreen();
                }

                // if a double click is detected......
                if (mouseEvent.getClickCount() == 2) {
                    //TODO
                }

                if (mouseEvent.isControlDown()) {
                    //TODO
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            Point newPt = new Point();

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if (ifSelected) {               //mouse drag starting from canvas - multiple selection
                    focusRect.setBounds(
                            Math.min(point.x, mouseEvent.getX()),
                            Math.min(point.y, mouseEvent.getY()),
                            Math.abs(point.x - mouseEvent.getX()),
                            Math.abs(point.y - mouseEvent.getY())
                    );

                    NetworkElement.selectFocusRect(elements, focusRect);
                }
                else if (!ifSelected && mouseEvent.isControlDown()) {               //mouse drag starting from some element when CTRL down
                    newPt.setLocation(
                            mouseEvent.getX() - point.x,
                            mouseEvent.getY() - point.y
                    );

                    NetworkElement.changePosition(elements, newPt);
                    point = mouseEvent.getPoint();
                }
                else if (!ifSelected && !mouseEvent.isControlDown()) {               //mouse drag starting from some element when CTRL not down
                    newPt.setLocation(mouseEvent.getPoint());

                    dragInitialPoint = point;
                    dragNewPoint = newPt;
                    flagDragConnection = true;
                    drawScreen();
                }

                mouseEvent.getComponent().repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(new Color(0xFFFFFF));
        graphics.fillRect(0, 0, getWidth(), getHeight());

        if (flagDragConnection) {
            graphics.setColor(new Color(0xFF0000));
            graphics.drawLine(dragInitialPoint.x, dragInitialPoint.y, dragNewPoint.x, dragNewPoint.y);
        }

        for (NetworkConnection connection : connections)
            connection.draw(graphics);

        for (NetworkElement element : elements)
            element.draw(graphics);
    }

    private void drawScreen() {
        this.repaint();
    }

    private BaseStationElement createBaseStationElementInstance(NetworkElement associatedNetworkElement) {
        BaseStationElement BaseStationElement = new BaseStationElement(associatedNetworkElement);
        listOfBaseStationElements.add(BaseStationElement);
        return BaseStationElement;
    }

    private ClientElement createScalingInstance(NetworkElement associatedNetworkElement) {
        ClientElement ClientElement = new ClientElement(associatedNetworkElement);
        listOfClientElements.add(ClientElement);
        return ClientElement;
    }

    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("PhySec Demonstrator");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                startScreen phySec = new startScreen();
                frame.add(new JScrollPane(phySec), BorderLayout.CENTER);
                frame.pack();
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
            }
        });
    }
}
