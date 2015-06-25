package model;

public enum Move {
	L, R, U, D, W, A, S;

	public static Move getMove(int candidate) {
		switch (candidate) {
		case 0:
			return L;
		case 1:
			return R;
		case 2:
			return U;
		case 3:
			return D;
		case 4:
			return W;
        case 6:
            return S;
		default:
		case 5:
			return A;
		}
	}

    public int newX(int oldX) {
        if (this == L) return oldX - 1;
        if (this == R) return oldX + 1;
        return oldX;
    }

    public int newY(int oldY) {
        if (this == D) return oldY - 1;
        if (this == U) return oldY + 1;
        return oldY;
    }
}