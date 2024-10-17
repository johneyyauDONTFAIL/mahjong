import java.awt.*;
import java.util.List;
import javax.swing.*;

public class ClientGUI extends JFrame {
    private Client client;
    private JPanel playerCardsPanel;
    private JPanel boardCardsPanel;

    public ClientGUI(Client client) {
        this.client = client;
        setTitle(client.getPlayer().getName() + "'s Game");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        playerCardsPanel = new JPanel();
        playerCardsPanel.setBorder(BorderFactory.createTitledBorder("Your Cards"));
        playerCardsPanel.setLayout(new FlowLayout());

        boardCardsPanel = new JPanel();
        boardCardsPanel.setBorder(BorderFactory.createTitledBorder("Cards on Board"));
        boardCardsPanel.setLayout(new FlowLayout());

        // Add playerCardsPanel to the bottom half
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        add(playerCardsPanel, gbc);

        // Add boardCardsPanel to the top half
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.5;
        add(boardCardsPanel, gbc);

        updatePlayerCards();
        updateBoardCards();

        setVisible(true);
    }

    public void updatePlayerCards() {
        playerCardsPanel.removeAll();
        List<Card> playerCards = client.getPlayer().getHandCards();
        for (Card card : playerCards) {
            JTextArea cardLabel = new JTextArea(card.toAsciiString());
            cardLabel.setEditable(false);
            cardLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
            playerCardsPanel.add(cardLabel);
        }
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
    }

    public void updateBoardCards() {
        boardCardsPanel.removeAll();
        List<Card> boardCards = client.getCardsOnTable();
        for (Card card : boardCards) {
            JTextArea cardLabel = new JTextArea(card.toAsciiString());
            cardLabel.setEditable(false);
            cardLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
            boardCardsPanel.add(cardLabel);
        }
        boardCardsPanel.revalidate();
        boardCardsPanel.repaint();
    }

    // public static void main(String[] args) {
    //     Client client = new Client("PlayerName", "f");
    //     SwingUtilities.invokeLater(() -> new ClientGUI(client));
    // }
}