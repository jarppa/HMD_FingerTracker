/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */

package uni.oulu.fingertracker.model;

public interface FingerTrackStateListener {
	void OnFingerTrackStateChanged(int state);
	void OnFingerTrackChanged(int what);
}
