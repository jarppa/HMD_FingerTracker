package uni.oulu.fingertracker.model;

import java.util.Map;

public interface PatternStorer {

	public DirectionPattern getPattern(String id);
	public void putPattern(String id, DirectionPattern dp);
	public void save();
	public void load();
	public Map<String,DirectionPattern> getPatterns();
}
