	package domain;

public enum Operator {
    lambda(1),
    id0(0),
    id1(0),
    id2(0),
    zero(0),
    one(1),
	not(1),
	shl1(1),
	shr1(1),
	shr4(1),
	shr16(1),
	and(2),
	or(2),
	xor(2),
	plus(2),
	if0(3),
	fold(3),
	tfold(1),//what is tfold??? - fold x 0!!!
    bonus(100); // fake operator reported by server to indicate special class of problems
	
	private int arity;

	Operator(int arity) {
		this.arity = arity;
	}
	
	public boolean isBinary() {
		return arity == 2;
	}
	
	public boolean isUnary() {
		return arity == 1;
	}

    public int getCardinality() {
        return arity;
    }

    public static Operator[] fromStrings(String []ops) {
        Operator [] opNames = new Operator[ops.length];
        for(int i=0;i<opNames.length;i++) opNames[i] = Operator.valueOf(ops[i]);
        return opNames;
    }

    public static String[] toStrings(Operator []ops) {
        String [] opNames = new String[ops.length];
        for(int i=0;i<opNames.length;i++) opNames[i] = ops[i].toString();
        return opNames;
    }
}
