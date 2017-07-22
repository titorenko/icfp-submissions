package algo;

import java.util.LinkedHashMap;
import java.util.Map;

class LruCache<A, B> extends LinkedHashMap<A, B> {
	private static final long serialVersionUID = 7191110822961230798L;
	private final int maxEntries;

    public LruCache(int maxEntries) {
        super(maxEntries + 1, 1.0f, true);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
        return super.size() > maxEntries;
    }
}