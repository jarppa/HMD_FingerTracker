/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-2014
 *
 */

package uni.oulu.fingertracker.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;

import uni.oulu.fingertracker.communicator.HmdCommunicator;

import android.content.Context;

public class FingerTrackModel {
	
	public static final String DIR_GOAL_KEY = "GOAL";
	public static final String DIR_START_KEY = "START";
	public static final String DIR_RIGHT_KEY = "RIGHT";
	public static final String DIR_LEFT_KEY = "LEFT";
	public static final String DIR_UP_KEY = "UP";
	public static final String DIR_DOWN_KEY = "DOWN";
	public static final String DIR_LEFT_DOWN_KEY = "LEFT_DOWN";
	public static final String DIR_RIGHT_DOWN_KEY = "RIGHT_DOWN";
	public static final String DIR_LEFT_UP_KEY = "LEFT_UP";
	public static final String DIR_RIGHT_UP_KEY = "RIGHT_UP";
	public static final String DIR_NONE_KEY = "NONE";
	
	public static final String [] DIRECTION_KEYS = {DIR_GOAL_KEY,
		DIR_START_KEY,
		DIR_RIGHT_KEY,
		DIR_LEFT_KEY,
		DIR_UP_KEY,
		DIR_DOWN_KEY,
		DIR_LEFT_DOWN_KEY,
		DIR_RIGHT_DOWN_KEY,
		DIR_LEFT_UP_KEY,
		DIR_RIGHT_UP_KEY,
		DIR_NONE_KEY
	};
	
	public static final int TARGET_MODE = 0;
	public static final int PATH_MODE = 1;
	
	public static final int STATE_CREATING = 1;
	public static final int STATE_TRACKING = 2;
	public static final int STATE_DONE = 3;
	
	public static final int TRACK_NODE_ADDED = 1;
	public static final int TRACK_NODE_REMOVED = 2;
	public static final int TRACK_CLEARED = 3;
	public static final int TRACK_SET = 4;
	
	private int state = STATE_CREATING;
	
	private TrackNode nodes [][];
	private FingerTrack track;
	private ArrayList <FingerTrackStateListener> listeners = new ArrayList<FingerTrackStateListener>();
	private ArrayList <HmdCommunicator> comms = new ArrayList<HmdCommunicator>();
	
	private TrackLogger logger;
	
	private int rows,cols,crow,ccol,elems = 0;
	
	private int track_mode = PATH_MODE;
	
	private boolean DEBUG = false;
	
	public FingerTrackModel(Context c, int cols, int rows) {
		this.rows = rows;
		this.cols = cols;
		Date now = new Date();
		
		logger = new TrackLogger(c, String.valueOf(now.getTime()));
		logger.start();
		logger.log("Model created", String.valueOf(rows)+" "+String.valueOf(cols));
		
		track = new PathTrack(this);
		
		nodes = new TrackNode[rows][cols];
		
	}
	
	public void finish() {
		if (logger != null) {
			logger.stop();
			logger = null;
		}	
	}
	
	public void restart(TrackCoord[] c) {
		track.clear();
		//clearTrack();
		setTrack(c);
		//if (!validateTrack())
		if(!track.validate())
		    reset();
		else {
			tellTrackChanged(TRACK_SET);
			setState(STATE_CREATING);
		}
	}
	
	/*public Map<String, Long> getDirections() {
		return directions;
	}*/
	
	/*public void setDirdata(Hashtable<String,Long> dirs) {
		directions.clear();
		directions.put(DIR_GOAL_KEY, dirs.get(DIR_GOAL_KEY));
		directions.put(DIR_START_KEY,dirs.get(DIR_START_KEY));
		directions.put(DIR_RIGHT_KEY,dirs.get(DIR_RIGHT_KEY));
		directions.put(DIR_LEFT_KEY,dirs.get(DIR_LEFT_KEY));
		directions.put(DIR_UP_KEY,dirs.get(DIR_UP_KEY));
		directions.put(DIR_DOWN_KEY,dirs.get(DIR_DOWN_KEY));
		directions.put(DIR_LEFT_DOWN_KEY,dirs.get(DIR_LEFT_DOWN_KEY));
		directions.put(DIR_RIGHT_DOWN_KEY,dirs.get(DIR_RIGHT_DOWN_KEY));
		directions.put(DIR_LEFT_UP_KEY,dirs.get(DIR_LEFT_UP_KEY));
		directions.put(DIR_RIGHT_UP_KEY,dirs.get(DIR_RIGHT_UP_KEY));
		directions.put(DIR_NONE_KEY,dirs.get(DIR_NONE_KEY));
	}*/
	
	public void attachStateListener(FingerTrackStateListener l) {
		listeners.add(l);
		tellStateChanged();
	}
	
	public void removeStateListener(FingerTrackStateListener l) {
		try {
			listeners.remove(l);
		}catch(NoSuchElementException nse){
			//FIXME: Do something?
		}
	}
	
	public void addCommunicator(HmdCommunicator c) {
		comms.add(c);
	}
	
	public void removeCommunicator(HmdCommunicator c) {
		try {
			comms.remove(c);
		}catch (Exception e) {
			//It's not there
		}
	}
	
	public int getRows() {
		return rows;
	}
	public int getCols() {
		return cols;
	}
	
	public TrackNode[][] getNodes() {
		return nodes;
	}
	
	public TrackNode getNode(TrackCoord c) {
		if (c.x < 0 || c.x > cols-1)
			return null;
		else if(c.y < 0 || c.y > rows-1)
			return null;
		
		return nodes[c.x][c.y];
	}
	
	public int getState() {
		return state;
	}
	
	public void setMode(int mode) {
		track_mode = mode;
		reset();
	}
	
	public int getMode() {
		return track_mode;
	}
	
	public void addNode(TrackNode n) {
		n.setId(elems);
		nodes[crow][ccol]=n;
		elems += 1;
		ccol = (ccol+1)%cols;
		crow = elems/cols;
	}
	
	public void fingerRaised() {
		if (state == STATE_TRACKING)
			logger.log("Finger raised");
		if (state == STATE_TRACKING)
			tellComms(DIR_NONE_KEY);
	}
	
	public boolean addTrackNode(TrackNode v) {
		
		if (state == STATE_CREATING) {
			if (track.addNode(v))
				tellTrackChanged(TRACK_NODE_ADDED);
		}
		else if (state == STATE_TRACKING)
			return false;
		else if (state == STATE_DONE) {
			startCreating();
			return true;
		}
		return false;
	}
	
	public boolean removeNodeFromTrack(TrackNode v) {
		try {
			if (track.removeNode(v)) {
				if (track.isEmpty())
					tellTrackChanged(TRACK_CLEARED);
				else
					tellTrackChanged(TRACK_NODE_REMOVED);
				return true;
			}
		}catch (NoSuchElementException nse){
			clearTrack();
			startCreating();
			return false;
		}
		return false;
	}
	
	public void clearTrack() {
		track.clear();
		clearDirections();
		clearAllVisited();
		tellTrackChanged(TRACK_CLEARED);
	}
	
	public void startCreating() {
		reset();
	}
	
	public void startTracking() {

		logger.log("Track is: ", trackToArray());
		logger.log("Start tracking");
		clearAllVisited();
		setState(STATE_TRACKING);
	}
	
	public void setTrackReady() {
		if (!track.validate()) {
			reset();
		}
		else {
			track.trackDone();
			//logger.log("Track", trackToArray());
			track.setDirections();
			startTracking();
		}
	}
	
	public void setTrackMode(int mode) {
		track_mode = mode;
	}
	
	public boolean isTracking() {
		return (state == STATE_TRACKING) ? true : false;
	}
	
	public boolean isCreating() {
		return (state == STATE_CREATING) ? true : false;
	}
	
	public boolean nodeInTrack(TrackNode n) {
		return track.nodeInTrack(n);
	}
	
	public boolean nodeVisited(TrackNode n) {
		logger.log("Cell ("+getNodeLoc(n).x+","+getNodeLoc(n).y+") visited");
		sendDir(n);
		if (track.visitInOrder(n)) {
			setVisited(n);
			return true;
		}
		return false;
	}
	
	public int getDirContextIndex() {
		/*if (track_mode == TARGET_MODE)
			return 0;*/
		return track.getIndexOfNode(track.getNextUnvisited());
		//return track.indexOf(getNextUnvisitedInTrack());
	}
	
	public TrackCoord[] getTrack() {
		TrackCoord [] ret = new TrackCoord[track.getSize()];
		for (int i=0; i<track.getSize(); i++) {
			ret[i] = getNodeLoc(track.getNodeInIndex(i));
		}
		return ret;
	}
	
	public void setDebug(boolean on) {
		DEBUG = on;
	}
	
	public boolean getDebug() {
		return DEBUG;
	}
	
	public boolean trackValid() {
		return track.validate();
	}
	
	public String getLogText() {
		if (logger != null)
			return logger.getLogs();
		
		return null;
	}
	
	private void setTrack(TrackCoord[] coords) {
		for (TrackCoord c : coords) {
			if (c.x < cols && c.x >= 0 && c.y < rows && c.y >= 0)
				addTrackNode(nodes[c.y][c.x]);
			else
				return;
		}
		/*if (!validateTrack())
			reset();*/
	}
	
	public void clearLogs() {
		if (logger != null)
			logger.clearLogs();
	}
	
	private String[] trackToArray() {
		String ret[] = new String[track.getSize()];
		for (int i=0;i<track.getSize();i++){
			ret[i] = getNodeLoc(track.getNodeInIndex(i)).toString();
		}
		return ret;
	}
	
	private TrackCoord getNodeLoc(TrackNode n) {
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				if (n == nodes[i][j])
					return new TrackCoord(j,i);
			}
		}
		return null;
	}
	
	private void sendDir(TrackNode n) {
		if (track_mode == TARGET_MODE)
			tellComms(n.getData());
		else
			tellComms(n.getData(track.getIndexOfNode(track.getNextUnvisited())));
	}
	
	private void tellComms(String data) {
		for (HmdCommunicator h : comms) {
			h.sendData(data);
		}
	}
	
	private void reset() {
		logger.stop();
		logger.start();
		tellComms(DIR_NONE_KEY);
		clearTrack();
		setState(STATE_CREATING);
	}
	
	private void clearAllVisited() {
		int i, j = 0;
		for (i=0; i<rows; i++) {
			for (j=0; j<cols; j++) {
				nodes[i][j].setChecked(false);
			}
		}
	}
	
	
	private void setVisited(TrackNode n) {
		n.setChecked(true);
		if (n.isGoal()) { //nodeIsGoal(n)) {
			setState(STATE_DONE);
		}	
	}
	
	private void clearDirections() {
		int i, j = 0;
		for (i=0; i<rows; i++) {
			for (j=0; j<cols; j++) {
				nodes[i][j].clearData();
				nodes[i][j].setDirToNext(DIR_NONE_KEY);
				nodes[i][j].setGoal(false);
				nodes[i][j].setStart(false);
			}
		}
	}
	
	protected String getDirectionTo(TrackNode from, TrackNode to) {
		int to_x = getNodeColPos(to);
		int to_y = getNodeRowPos(to);
		int from_x = getNodeColPos(from);
		int from_y = getNodeRowPos(from);
		int dist_x;
		int dist_y;
		boolean l = false;
		boolean r = false;
		boolean u = false;
		boolean d = false;
		
		if (from == to) {
			if (to.isGoal())
				return DIR_GOAL_KEY;
			else
				return DIR_START_KEY;
		}
		dist_x = to_x - from_x;
		dist_y = to_y - from_y;
		if (dist_x < 0) { //left
			l = true;
		}
		else if (dist_x > 0) { //right
			r = true;
		}
		
		if (dist_y < 0) { //up
			u = true;
		}
		else if (dist_y > 0){ //down
			d = true;
		}
		
		if ((l || r) && (u || d)) { //corner
			if (l && u)
				return DIR_LEFT_UP_KEY;
			else if (l && d)
				return DIR_LEFT_DOWN_KEY;
			else if (r && u)
				return DIR_RIGHT_UP_KEY;
			else if (r && d)
				return DIR_RIGHT_DOWN_KEY;
		}
		else if (l)
			return DIR_LEFT_KEY;
		else if (r)
			return DIR_RIGHT_KEY;
		else if (u)
			return DIR_UP_KEY;
		else if (d)
			return DIR_DOWN_KEY;
		
		return DIR_NONE_KEY;
	}
	
	private int getNodeColPos(TrackNode n) {
		int i, j = 0;
		for (i=0; i<rows; i++) {
			for (j=0; j<cols; j++) {
				if (nodes[i][j] == n)
					return j;
			}
		}
		return -1;
	}
	
	private int getNodeRowPos(TrackNode n) {
		int i, j = 0;
		for (i=0; i<rows; i++) {
			for (j=0; j<cols; j++) {
				if (nodes[i][j] == n)
					return i;
			}
		}
		return -1;
	}
	
	/*
	 * Check if the node n is adjacent to node adj.
	 * Returns: The direction information to n from adj or DIR_NONE if not adjacent.
	 */
	protected String isAdjacent(TrackNode adj, TrackNode n) {
		int i, j = 0;
		for (i=0; i<rows; i++) {
			for (j=0; j<cols; j++) {
				if (nodes[i][j] == n) {
					if ( i == 0) { //on first row
						
						if (j == 0) { //on first col
							if (nodes[i][j+1] == adj)
								return DIR_LEFT_KEY;
							else if (nodes[i+1][j] == adj)
								return DIR_UP_KEY;
							else if (nodes[i+1][j+1] == adj)
								return DIR_LEFT_UP_KEY;
						}
						else if (j == cols-1) {  //on last col
							if (nodes[i][j-1] == adj)
								return DIR_RIGHT_KEY;
							else if(nodes[i+1][j-1] == adj)
								return DIR_RIGHT_UP_KEY;
							else if(nodes[i+1][j] == adj)
								return DIR_UP_KEY;
						}
						else {
				    		if (nodes[i+1][j-1] == adj)
				    			return DIR_RIGHT_UP_KEY;
				    		else if (nodes[i+1][j] == adj)
				    			return DIR_UP_KEY;
				    		else if (nodes[i+1][j+1] == adj)
				    			return DIR_LEFT_UP_KEY;
				    		else if (nodes[i][j-1] == adj)
				    			return DIR_RIGHT_KEY;
				    		else if (nodes[i][j+1] == adj)
								return DIR_LEFT_KEY;
				    	}
					}	
					else if ( i == rows-1) { //on last row
				    	
				    	if (j == 0) { //on first col
				    		if (nodes[i-1][j] == adj)
				    			return DIR_DOWN_KEY;
				    		else if(nodes[i-1][j+1] == adj)
				    			return DIR_LEFT_DOWN_KEY;
				    		else if(nodes[i][j+1] == adj)
								return DIR_LEFT_KEY;
				    	}
				    	else if (j == cols-1) { //on last col
							if (nodes[i-1][j-1] == adj)
								return DIR_RIGHT_DOWN_KEY;
							else if (nodes[i-1][j] == adj)
								return DIR_DOWN_KEY;
							else if(nodes[i][j-1] == adj)
								return DIR_RIGHT_KEY;
						}
				    	else {
				    		if (nodes[i-1][j-1] == adj)
				    			return DIR_RIGHT_DOWN_KEY;
				    		else if(nodes[i-1][j] == adj)
				    			return DIR_DOWN_KEY;
				    		else if(nodes[i-1][j+1] == adj)
				    			return DIR_LEFT_DOWN_KEY;
				    		else if(nodes[i][j-1] == adj)
				    			return DIR_RIGHT_KEY;
				    		else if(nodes[i][j+1] == adj)
								return DIR_LEFT_KEY;
				    	}
				    }
					else {
						if (j == 0) { //on first col
				    		if (nodes[i-1][j] == adj)
				    			return DIR_DOWN_KEY;
				    		else if(nodes[i-1][j+1] == adj)
				    			return DIR_LEFT_DOWN_KEY;
				    		else if(nodes[i][j+1] == adj)
				    			return DIR_LEFT_KEY;
				    		else if(nodes[i+1][j] == adj)
				    			return DIR_UP_KEY;
				    		else if(nodes[i+1][j+1] == adj)
								return DIR_LEFT_UP_KEY;
				    	}
						else if (j == cols-1) { //on last col
							if (nodes[i-1][j-1] == adj)
								return DIR_RIGHT_DOWN_KEY;
							else if(nodes[i-1][j] == adj)
								return DIR_DOWN_KEY;
							else if(nodes[i][j-1] == adj)
								return DIR_RIGHT_KEY;
							else if(nodes[i+1][j-1] == adj)
								return DIR_RIGHT_UP_KEY;
							else if(nodes[i+1][j] == adj)
								return DIR_UP_KEY;
						}
				    	else {
				    		if (nodes[i-1][j-1] == adj)
				    			return DIR_RIGHT_DOWN_KEY;
				    		else if(nodes[i-1][j] == adj)
				    			return DIR_DOWN_KEY;
				    		else if(nodes[i-1][j+1] == adj)
				    			return DIR_LEFT_DOWN_KEY;
				    		else if(nodes[i][j-1] == adj)
				    			return DIR_RIGHT_KEY;
				    		else if(nodes[i][j+1] == adj)
				    			return DIR_LEFT_KEY;
				    		else if(nodes[i+1][j-1] == adj)
				    			return DIR_RIGHT_UP_KEY;
				    		else if(nodes[i+1][j] == adj)
				    			return DIR_UP_KEY;
				    		else if(nodes[i+1][j+1] == adj)
								return DIR_LEFT_UP_KEY;
				    	}
					}
				}
			}
		}
		
		return DIR_NONE_KEY;
	}
	
	private void setState(int s) {
		state = s;
		tellStateChanged();
	}
	
	private void tellStateChanged() {
		
		if (listeners.isEmpty())
			return;
		
		for (FingerTrackStateListener l : listeners) {
			l.OnFingerTrackStateChanged(state);
		}
	}
	
	private void tellTrackChanged(int what) {
		if (listeners.isEmpty())
			return;
		
		for (FingerTrackStateListener l : listeners) {
			l.OnFingerTrackChanged(what);
		}
	}
}
