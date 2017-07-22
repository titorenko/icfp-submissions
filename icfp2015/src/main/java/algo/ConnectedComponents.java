package algo;

import model.Board;

public class ConnectedComponents {
	private final Board g;
	private final boolean[] isVisited;
	private final IntQueue q;
	
	private int nComponents;

	public ConnectedComponents(Board g) {
		this.g = g;
		this.isVisited = new boolean[getVertexCount(g)];
		this.nComponents = 0;
		this.q = new BoundedIntQueue(getVertexCount(g));
	}
	
	private int getVertexCount(Board g) {
		return g.getHeight()*g.getWidth();
	}

	public int compute() {
		for (int i = 0; i < getVertexCount(g); i++) {
			if (g.isSet(i)) continue;
			if(!isVisited[i]) {
				nComponents++;
				bfs(i);
			}		
		}
		return nComponents;
	}

	private void bfs(int i) {
		q.add(i);
		isVisited[i] = true;
		while(!q.isEmpty()) {
			int v1= q.poll();
			adj(g, v1).forAll(v2 -> { 
				if (!isVisited[v2]) {
					isVisited[v2] = true;
					q.add(v2);				
				}
			});
		}
	}

	private IntArrayList adj(Board g, int v){
		return g.getPossibleMovesFrom(v);
	}
	
}