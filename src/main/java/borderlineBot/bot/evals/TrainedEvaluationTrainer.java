package borderlineBot.bot.evals;

import borderlineBot.bot.Bot;
import borderlineBot.bot.bots.BasicAlphaBetaNegaMaxBot;
import borderlineBot.bot.bots.EvaluateOnePlyBot;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.Game;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.util.RNG;
import borderlineBot.util.Tuple;

/** Training the Trained Evaluation Function */
public class TrainedEvaluationTrainer{
	
	/** The size of the population */
	private static final int POPULATION = 25;
	
	/** The fraction of the population which represents the Elite */
	private static final float ELITE = 0.6f;
	
	/** Number of ply after which game is declared a draw */
	private static final int DRAW = 100;
	
	/** Mutation Rate for crossover */
	private static final float MUTATION_RATE = 0.2f;
	
	
	/** Initializes Training procedure for TrainedEval */
	public static void trainEvaluation(){
		//Init
		TrainedEvaluation[] pop = new TrainedEvaluation[POPULATION];
		for(int c=0; c<pop.length; c++){
			pop[c] = new TrainedEvaluation();
		}
		//Execute training
		int generation = 0;
		final int[] threadCounter = new int[]{0, 0};
		while(true){
			System.out.println("Gen: "+generation+" Weigths: "+pop[0]);
			final Tuple<Bot, Integer>[] comps = new Tuple[pop.length];
			for(int c=0; c<comps.length; c++){
				comps[c] = new Tuple<Bot, Integer>(injectEval(pop[c]), 0);
			}
			threadCounter[0] = 0;
			threadCounter[1] = 0;
			for(int c=0; c<comps.length; c++){
				for(int c2=c+1; c2<comps.length; c2++){
					final Tuple<Integer, Integer>[] gameComps = new Tuple[]{new Tuple<Integer, Integer>(c, c2), new Tuple<Integer, Integer>(c2, c)};
					for(int c3=0; c3<gameComps.length; c3++){
						final int cc = c3;
						Thread t = new Thread(new Runnable(){
							public void run(){
								Game game = new Game(comps[gameComps[cc].getA()].getA(), comps[gameComps[cc].getB()].getA());
								int drawCounter = DRAW;
								while(!game.gameOver() && drawCounter>0){
									drawCounter--;
									game.nextTurn();
								}
								if(game.gameOver()){
									Tuple<Bot, Integer> winner = game.getWinner()==0?comps[gameComps[cc].getA()]:comps[gameComps[cc].getB()];
									winner.setB(winner.getB()+1);
								}
								threadCounter[0]++;
							}
						});
						threadCounter[1]++;
						t.start();
					}
				}
			}
			//Wait till all matches have ended
			while(threadCounter[0]<threadCounter[1]){
				try{Thread.sleep(10);}catch(Exception ex){}
			}
			//sort the population
			for(int c=0; c<comps.length; c++){
				for(int c2=c+1; c2<comps.length; c2++){
					if(comps[c].getB()<comps[c2].getB()){
						Tuple<Bot, Integer> help = comps[c];
						TrainedEvaluation popHelp = pop[c];
						comps[c] = comps[c2];
						pop[c] = pop[c2];
						comps[c2] = help;
						pop[c2] = popHelp;
					}
				}
			}
			//Replace inferior part of the population
			for(int c=(int)(pop.length*ELITE); c<pop.length; c++){
				pop[c] = crossover(pop[RNG.nextInt((int)(pop.length*ELITE))], pop[RNG.nextInt((int)(pop.length*ELITE))]);
			}
			generation++;
		}
	}
	
	/** Actually builds a Bot based on the given Evaluation function */
	private static Bot injectEval(TrainedEvaluation eval){
		Bot bot = new BasicAlphaBetaNegaMaxBot(new MoveOrderer.DefaultMoveOrder(), eval, 2);
		return bot;
	}
	
	/** crosses over two TrainedEvaluations */
	private static TrainedEvaluation crossover(TrainedEvaluation mother, TrainedEvaluation father){
		int[] weights = new int[mother.getNeededWeights()];
		for(int c=0; c<weights.length; c++){
			weights[c] = RNG.nextBoolean()?mother.getWeights()[c]:father.getWeights()[c];
			if(RNG.nextFloat()<MUTATION_RATE){
				weights[c] = TrainedEvaluation.randWeight();
			}
		}
		TrainedEvaluation child = new TrainedEvaluation(weights);
		return child;
	}
	
}
