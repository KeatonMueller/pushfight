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
import main.java.agents.alphaBeta.AlphaBetaAgent;
import main.java.agents.mcts.MonteCarloAgent;
// import main.java.agents.oep.OEPAgent;

public class PlayerSelect extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int NUM_OPTIONS = 5;
    private Agent p1, p2;

    public PlayerSelect() {
        List<List<JRadioButton>> buttons = new ArrayList<>();
        JRadioButton human, random, alpha, stoch, mcts; // , oep;
        List<JRadioButton> options;

        int i;
        for (i = 0; i < 2; i++) {
            human = new JRadioButton("Human");
            random = new JRadioButton("Random");
            alpha = new JRadioButton("Alpha Beta");
            stoch = new JRadioButton("Stochastic Alpha Beta");
            mcts = new JRadioButton("MCTS");
            // oep = new JRadioButton("Online Evolutionary Planning");

            options = new ArrayList<>();
            options.add(human);
            options.add(random);
            options.add(alpha);
            options.add(stoch);
            options.add(mcts);
            // options.add(oep);

            instrument(human, options, null, i);
            instrument(random, options, new RandomAgent(), i);
            instrument(alpha, options, new AlphaBetaAgent(), i);
            instrument(stoch, options, new AlphaBetaAgent(AlphaBetaAgent.ABType.STOCHASTIC), i);
            instrument(mcts, options, new MonteCarloAgent(), i);
            // instrument(oep, options, new OEPAgent(), i);

            human.setSelected(true);
            buttons.add(options);
        }

        setLayout(new GridLayout(NUM_OPTIONS + 1, 2));
        add(new JLabel("Player 1"));
        add(new JLabel("Player 2"));
        for (i = 0; i < NUM_OPTIONS; i++) {
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
