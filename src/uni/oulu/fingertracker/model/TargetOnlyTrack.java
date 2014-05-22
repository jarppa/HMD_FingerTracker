/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */

package uni.oulu.fingertracker.model;

public class TargetOnlyTrack extends FingerTrack {
	private FingerTrackModel mModel;
	public TargetOnlyTrack(FingerTrackModel model) {
		super();
		mModel = model;
	}
	@Override
	public boolean visitNode(TrackNode n) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean finished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trackDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addNode(TrackNode n) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeNode(TrackNode n) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean nodeInTrack(TrackNode n) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getIndexOfNode(TrackNode n) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public TrackNode getNextUnvisited() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean visitInOrder(TrackNode n) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public TrackNode getNodeInIndex(int i) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void setDirections() {
		// TODO Auto-generated method stub
		
	}

}
