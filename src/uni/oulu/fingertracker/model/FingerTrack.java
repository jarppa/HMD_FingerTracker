/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */

package uni.oulu.fingertracker.model;

import java.util.ArrayList;
import java.util.Iterator;

abstract public class FingerTrack {

	protected ArrayList <TrackNode> track;
	
	public FingerTrack(){
		track = new ArrayList<TrackNode>();
	}
	
	abstract public boolean visitNode(TrackNode n);
	abstract public void trackDone();
	abstract public boolean addNode(TrackNode n);
	abstract public boolean finished();
	abstract public boolean validate();
	abstract public boolean removeNode(TrackNode n);
	abstract public boolean isEmpty();
	abstract public void clear();
	abstract public boolean nodeInTrack(TrackNode n);
	abstract public int getIndexOfNode(TrackNode n);
	abstract public TrackNode getNextUnvisited();
	abstract public boolean visitInOrder(TrackNode n);
	abstract public int getSize();
	abstract public TrackNode getNodeInIndex(int i);
	
	abstract protected void setDirections();
	
	protected boolean isNextInPath(TrackNode n) {
		if (track.isEmpty())
			return false;
		
		Iterator <TrackNode>it = track.iterator();
		TrackNode node;
		//for (node = it.next();it.hasNext();node=it.next()) {
		do {
			node = it.next();
			if (n == node && !node.isChecked()) {
				if (node.isStart())
					return true;
				else if (track.get(track.indexOf(node)-1).isChecked()) {
					return true;
				}
				else
					return false;
			}
		}while(it.hasNext());
		return false;
	}
}
