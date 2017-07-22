package model;

public enum MoveEncoding {
	W(new char[]{'p', '\'', '!', '.', '0', '3'}),
	E(new char[]{'b', 'c', 'e', 'f', 'y', '2'}),
	SW(new char[]{'a', 'g', 'h', 'i', 'j', '4'}),
	SE(new char[]{'l', 'm', 'n', 'o', ' ', '5'}),
	CW(new char[]{'d', 'q', 'r', 'v', 'z', '1'}),
	CCW(new char[]{'k', 's', 't', 'u', 'w', 'x'});
	
	private static MoveEncoding[] encoding = new MoveEncoding[255];

    static {
         for(MoveEncoding moveEncoding: MoveEncoding.values()){
             for(char representation: moveEncoding.encodings){
            	 encoding[representation] = moveEncoding;
             }
         }
     }
			
	private char[] encodings;

	MoveEncoding(char[] encodings) {
		this.encodings = encodings;
	}
	
	public char getEncoding() {
		return encodings[0];
	}

    public static MoveEncoding encodingOf(char move){
        MoveEncoding moveEncoding = encoding[move];
        if(moveEncoding == null){
            throw new IllegalArgumentException("The move " + move + " is not supported");
        }
        return moveEncoding;
    }
}