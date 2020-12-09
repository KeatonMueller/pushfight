# pushfight

This is my Senior Project for my Computer Science degree at Yale University.

It involves creating an intelligent agent for the game of Push Fight,
a multi-action adversarial board game.

Currently you are able to play against another human, a random agent,
an agent employing the Minimax algorithm with Alpha Beta Pruning, a
stochastic version of the previous agent, or an agent using Monte Carlo
Tree Search.

There are more agent types and variations that can be analyzed using
the evaluation tool.

This project is coded using Java version 11.

## How to use it

This project requires you have the Java SDK installed. It was coded using Java 11,
but other versions may work as well.

You must navigate to the `src` directory to run any of the `make` commands.

### Playing Yourself
If you'd like to play the game yourself versus a computer or human opponent,
there are two options: text based or graphics based.

To play the game with graphics, run `make gui`.
To play the text-based version, run `make text`.

### Evaluation Tool
If you'd like to evaluate two different methods against one another, you can
run `make evaluate`. The console will prompt you with options allowing you
to select which agents to evaluate and for how many games.

If you want to evaluate the different weights from the various genetic
evolution techniques, you must select the Vanilla Alpha Beta agent. If you do,
you will be prompted to select which weights you'd like to use for the agent's 
heuristic.

### Evolution Tool
If you'd like to run the genetic evolution program, run `make evolve`. You will
be prompted to choose an evolution strategy as well as other configuration options,
like time limit and population size.

The results of the evolution are printed to `stdout`.

### Branching Factor Analysis
If you'd like to run a branching factor analysis, run `make analyze`. You will
just be prompted for a number of games to simulate, and then two Stochastic
Alpha Beta agents will play and the board states will be analyzed.