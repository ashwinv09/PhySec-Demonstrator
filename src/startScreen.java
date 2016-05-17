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
//    private List<NetworkElement> selectedElements = new ArrayList<NetworkElement>();
    private List<NetworkConnection> connections = new ArrayList<NetworkConnection>();
    private Point point = new Point(WIDTH/2, HEIGHT/2);
    private Rectangle focusRect = new Rectangle();
    private boolean ifSelected = false;

    static List<BaseStationElement> listOfBaseStationElements = new ArrayList<BaseStationElement>();
    static BaseStationElement newBaseStationElement;

    static List<ClientElement> listOfClientElements = new ArrayList<ClientElement>();
    static ClientElement newClientElement;

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
                boolean flagS = false;

                if (mouseEvent.isControlDown()) {
                    // checking if focus is lost amidst path setting

                    for (BaseStationElement BaseStationElement : listOfBaseStationElements) {
                        if (BaseStationElement.associatedNetworkElement.contains(point)) {
                            flagS = true;
                            break;
                        }
                    }

                    if (!flagS) {
                        for (ClientElement ClientElement : listOfClientElements) {
                            if (ClientElement.associatedNetworkElement.contains(point)) {
                                break;
                            }
                        }
                    }
                }

                if (mouseEvent.isShiftDown())
                    NetworkElement.changeSelectStatus(elements, point);
                else if (NetworkElement.selectOneElement(elements, point))
                    ifSelected = false;
                else {
                    NetworkElement.unselectAllElements(elements);
                    ifSelected = true;
                }

                mouseEvent.getComponent().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                ifSelected = false;
                focusRect.setBounds(0, 0, 0, 0);
                mouseEvent.getComponent().repaint();
            }

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                point = mouseEvent.getPoint();

                NetworkElement.unselectAllElements(elements);
                Point centerPoint = point.getLocation();
                NetworkElement element = new NetworkElement(centerPoint, NetworkElementType.CLIENT_ELEMENT);
                element.setSelected(true);
                elements.add(element);
                newClientElement = createScalingInstance(element);
                drawScreen();

                // if a double click is detected......
                if (mouseEvent.getClickCount() == 2) {
                    for (BaseStationElement s : listOfBaseStationElements) {
                        if (s.associatedNetworkElement.contains(point)) {
                            newBaseStationElement = s;
                            break;
                        }
                    }

                    for (ClientElement sc : listOfClientElements) {
                        if (sc.associatedNetworkElement.contains(point)) {
                            newClientElement = sc;
                            break;
                        }
                    }
                }

                if (mouseEvent.isControlDown()) {
                    for (BaseStationElement BaseStationElement : listOfBaseStationElements) {
                        if (BaseStationElement.associatedNetworkElement.contains(point)) {
                            break;
                        }
                    }

                    for (ClientElement ClientElement : listOfClientElements) {
                        if (ClientElement.associatedNetworkElement.contains(point)) {
                            break;
                        }
                    }
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            Point newPt = new Point();

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if (ifSelected) {
                    focusRect.setBounds(
                            Math.min(point.x, mouseEvent.getX()),
                            Math.min(point.y, mouseEvent.getY()),
                            Math.abs(point.x - mouseEvent.getX()),
                            Math.abs(point.y - mouseEvent.getY())
                    );

                    NetworkElement.selectFocusRect(elements, focusRect);
                }
                else {
                    newPt.setLocation(
                            mouseEvent.getX() - point.x,
                            mouseEvent.getY() - point.y
                    );

                    NetworkElement.changePosition(elements, newPt);
                    point = mouseEvent.getPoint();
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
        graphics.setColor(new Color(0x00f0f0f0));
        graphics.fillRect(0, 0, getWidth(), getHeight());

        for (NetworkConnection connection : connections)
            connection.draw(graphics);

        for (NetworkElement element : elements)
            element.draw(graphics);

        if(ifSelected) {
            graphics.setColor(Color.DARK_GRAY);
        }
    }

    public void drawScreen() {
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
