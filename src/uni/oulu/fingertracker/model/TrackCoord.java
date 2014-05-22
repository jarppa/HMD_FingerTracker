package uni.oulu.fingertracker.model;

public final class TrackCoord {

	public int x = 0;
	public int y = 0;
	
	public TrackCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "TrackCoord x:"+String.valueOf(x)+" y:"+String.valueOf(y);
	}
}
