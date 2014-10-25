package awap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.google.common.base.Optional;

public class Game {
	private State state;
	private Integer number;
	private Integer manual_steps = 0;
	private Integer failRound = 0;
	private Integer prevScore = 0;
	private boolean goBack = false;
	ArrayList<Point> bonusPoint = new ArrayList<Point>(); 
	public Game(){
		
		bonusPoint.add(new Point(2,9));
		bonusPoint.add(new Point(7,7)); 
		bonusPoint.add(new Point(10,2)); 
		bonusPoint.add(new Point(12,7)); 
		bonusPoint.add(new Point(17,10)); 
		bonusPoint.add(new Point(7,12)); 
		bonusPoint.add(new Point(12,12)); 
		bonusPoint.add(new Point(9,17)); 
	//Logger.log("length " + bonusPoint.size());
	}
	public Optional<Move> updateState(State newState) {
		if (newState.getError().isPresent()) {
			Logger.log(newState.getError().get());
			return Optional.absent();
		}

		if (newState.getMove() != -1) {
			return Optional.fromNullable(findMove());
		}

		state = newState;
		if (newState.getNumber().isPresent()) {
			number = newState.getNumber().get();
		}

		return Optional.absent();
	}
	
	private void manual_step(){
		
		switch (number){
		case 1:break;
		case 2:break;
		case 3:break;
		case 4:break;
		}
	}
	
	private boolean bonusCheck( Block block, Point p){
		//int N = state.getDimension();
		for (Point offset : block.getOffsets()) {
			Point q = offset.add(p);
			int x = q.getX(), y = q.getY();
			if (p.getX() == 11 && p.getY()==2){
				Logger.log("xy" +x + " " + y);
			}
			for (Point pa : bonusPoint){
			if (pa.getX() == q.getX() && pa.getY() == q.getY())
			   return true;
			}
		}
		return false;
	}
	private Move findMove() {
		int N = state.getDimension();
		List<Block> blocks = state.getBlocks().get(number);
		// List<Point> choices=new ArrayList<Point>();
		Collections.sort(blocks);
		int newRot = 0;
		int ithBlock = 0;
		Point p = new Point(0, 0);
		Point middlePoint = new Point(N / 2, N / 2);
		int maxScore = 0;
		int baseScore = 100;
		Point[] corners = { new Point(0, 0), new Point(N, 0), new Point(N, N),
				new Point(0, N) };
		Point corner = corners[number];
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) {
				for (int rot = 0; rot < 4; rot++) {
					for (int i = 0; i < blocks.size(); i++) {
						Point newP = new Point(x, y);
						if (canPlace(blocks.get(i).rotate(rot), new Point(x, y))) {
							
							int score = baseScore;
							if (bonusCheck(blocks.get(i).rotate(rot), new Point(x, y))){
								//Logger.log(x +" " + y);
								score += blocks.get(i).getOffsets().size() * 20;
								Logger.log("score " +  score);
								//return new Move(i, rot, newP.getX(), newP.getY());
							}
							
							
							
							if (!goBack) {
								score -= newP.distance(middlePoint);//find the point that's closest to middle point
								score += newP.distance(corner);//find the point that's farest from corner point
								List<Point> pointSet=blocks.get(i).getOffsets();
								int farestDistance=0;
								for(int ithLittleBlock=0;ithLittleBlock<pointSet.size();ithLittleBlock++){
								   if(pointSet.get(ithLittleBlock).add(newP).distance(corner)>farestDistance){
								      farestDistance=pointSet.get(ithLittleBlock).distance(corner);
								   }
								}
								score += farestDistance;
							}
							score += blocks.get(i).getOffsets().size()*5;//use the biggest size blocks
							
							if (score > maxScore) {
								p = newP;
								newRot = rot;
								ithBlock = i;
								maxScore = score;
							}
						}
					}
				}
			}
		}
		if (maxScore < prevScore) {
			failRound++;
			if (failRound >= 5) {
				//goBack = true;
				Logger.log("Going back!");
			}
		} else {
			failRound = 0;
		}
		prevScore = maxScore;
		// if(!p.equals(new Point(0,0))){
	//	Logger.log("postion:" + p.getX() + " " + p.getY() );
		return new Move(ithBlock, newRot, p.getX(), p.getY());
		// }
		// return new Move(0, 0, 0, 0);
	}

	private int getPos(int x, int y) {
		return state.getBoard().get(x).get(y);
	}

	private boolean canPlace(Block block, Point p) {
		boolean onAbsCorner = false, onRelCorner = false;
		int N = state.getDimension() - 1;

		Point[] corners = { new Point(0, 0), new Point(N, 0), new Point(N, N),
				new Point(0, N) };
		;
		Point corner = corners[number];

		for (Point offset : block.getOffsets()) {
			Point q = offset.add(p);
			int x = q.getX(), y = q.getY();

			if (x > N || x < 0 || y < 0 || y > N || getPos(x, y) >= 0
					|| getPos(x, y) == -2
					|| (x > 0 && getPos(x - 1, y) == number)
					|| (y > 0 && getPos(x, y - 1) == number)
					|| (x < N && getPos(x + 1, y) == number)
					|| (y < N && getPos(x, y + 1) == number)) {
				return false;
			}

			onAbsCorner = onAbsCorner || q.equals(corner);
			onRelCorner = onRelCorner
					|| (x > 0 && y > 0 && getPos(x - 1, y - 1) == number)
					|| (x < N && y > 0 && getPos(x + 1, y - 1) == number)
					|| (x > 0 && y < N && getPos(x - 1, y + 1) == number)
					|| (x < N && y < N && getPos(x + 1, y + 1) == number);
		}

		return !((getPos(corner.getX(), corner.getY()) < 0 && !onAbsCorner) || (!onAbsCorner && !onRelCorner));
	}
}
