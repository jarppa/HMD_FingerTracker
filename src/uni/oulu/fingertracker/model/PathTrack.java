/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */
package uni.oulu.fingertracker.model;

import java.util.NoSuchElementException;

public class PathTrack extends FingerTrack {

	//private ArrayList <TrackNode> track = new ArrayList<TrackNode>();
	private FingerTrackModel mModel = null;
	
	public PathTrack(FingerTrackModel model) {
		super();
		mModel = model;
		//track = new ArrayList<TrackNode>();
		
	}
	
	@Override
	public boolean visitNode(TrackNode n) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trackDone() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addNode(TrackNode n) {
		if (track.isEmpty()) {
			n.setStart(true);
			track.add(n);
			return true;
		}
		else {
			TrackNode last = track.get(track.size()-1);
			String d = mModel.isAdjacent(last, n);
		
			if (d != FingerTrackModel.DIR_NONE_KEY) {
				last.setDirToNext(d);
				last.setGoal(false);
				n.setDirToNext(FingerTrackModel.DIR_GOAL_KEY);
				n.setGoal(true);
				track.add(n);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeNode(TrackNode v) {
		try {
			if (track.get(track.size()-1) == v) {
				track.remove(v);
				if (v.isGoal())
					if (! track.isEmpty())
						track.get(track.size()-1).setGoal(true);
				return true;
			}
		}catch (NoSuchElementException nse){
			return false;
		}
		return false;
	}
	
	@Override
	public boolean isEmpty() {
		return track.isEmpty();
	}
	
	@Override
	public boolean finished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validate() {
		if (track.size() < 2) {
			return false;
		}
		else
			return true;
	}
	
	@Override
	public void clear() {
		for (TrackNode n : track) {
			n.clearData();
			n.setDirToNext("NONE");
			n.setGoal(false);
			n.setStart(false);
		}
		track.clear();
	}

	@Override
	public boolean nodeInTrack(TrackNode n) {
		return track.contains(n);
	}

	@Override
	public int getIndexOfNode(TrackNode n) {
		return track.indexOf(n);
	}

	@Override
	public TrackNode getNextUnvisited() {
		for (TrackNode n : track) {
			if (!n.isChecked())
				return n;
		}
		return null;
	}

	@Override
	public boolean visitInOrder(TrackNode n) {
		if (track.isEmpty())
			return false;
		
		if (isNextInPath(n))
			return true;
		
		return false;
		
	}

	@Override
	protected void setDirections() {
		TrackNode temp;
		int i = 0;
		int j = 0;
		int c = 0;
		TrackNode [][] nodes = mModel.getNodes();
		int rows = mModel.getRows();
		int cols = mModel.getCols();
		
		for (c=0;c<track.size();c++) {
			for (i=0; i<rows; i++) {
				for (j=0; j<cols; j++) {
					temp = nodes[i][j];
					if (temp.isGoal())
						temp.setData(FingerTrackModel.DIR_GOAL_KEY, track.size()-1);
					
					temp.setData(mModel.getDirectionTo(temp,track.get(c)),c);
				}
			}
		}
		
	}

	@Override
	public int getSize() {
		return track.size();
	}

	@Override
	public TrackNode getNodeInIndex(int i) {
		return track.get(i);
	}
	
	
}
