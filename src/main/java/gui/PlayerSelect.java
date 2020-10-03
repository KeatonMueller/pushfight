package main.java.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import main.java.agents.Agent;
import main.java.agents.RandomAgent;
import main.java.agents.AlphaBetaAgent;

public class PlayerSelect extends JPanel {
    private static final long serialVersionUID = 1L;
    private Agent p1, p2;

    public PlayerSelect() {
        List<List<JRadioButton>> buttons = new ArrayList<>();
        JRadioButton human;
        JRadioButton random;
        JRadioButton alpha;
        List<JRadioButton> options;

        int i;
        for (i = 0; i < 2; i++) {
            human = new JRadioButton("Human");
            random = new JRadioButton("Random");
            alpha = new JRadioButton("Alpha Beta");

            options = new ArrayList<>();
            options.add(human);
            options.add(random);
            options.add(alpha);

            instrument(human, options, null, i);
            instrument(random, options, new RandomAgent(), i);
            instrument(alpha, options, new AlphaBetaAgent(), i);

            human.setSelected(true);
            buttons.add(options);
        }

        setLayout(new GridLayout(4, 2));
        add(new JLabel("Player 1"));
        add(new JLabel("Player 2"));
        for (i = 0; i < 3; i++) {
            add(buttons.get(0).get(i));
            add(buttons.get(1).get(i));
        }
    }

    /**
     * Set up the JRadioButton with the proper action listener
     * 
     * @param button  The JRadioButton to instrument
     * @param options The JRadioButtons in the group
     * @param agent   The agent to assign if this option is selected
     * @param player  The player who is being assigned
     */
    private void instrument(JRadioButton button, List<JRadioButton> options, Agent agent,
            int player) {
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (button.isSelected()) {
                    if (player == 0)
                        p1 = agent;
                    else
                        p2 = agent;
                    for (JRadioButton other : options) {
                        if (other != button) {
                            other.setSelected(false);
                        }
                    }
                } else {
                    if (player == 0)
                        p1 = null;
                    else
                        p2 = null;
                }
            }
        });
    }

    /**
     * Return the selected players
     * 
     * @return The Agents that are the players (or null if agent is human)
     */
    public Agent[] getPlayers() {
        return new Agent[] {p1, p2};
    }
}
