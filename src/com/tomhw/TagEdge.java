package com.tomhw;

public class TagEdge {
	String name;
	float score;
	String rel;
	int cnt;

	TagEdge(String n, float w, String r) {
		name = n;
		score = w;
		rel = r;
		cnt = 1;
	}

	void cntadd() {
		cnt++;
	}

	@Override
	protected	TagEdge clone() {
		TagEdge r = new TagEdge(name, score, rel);
		r.cnt = cnt;
		return r;
	}
}
