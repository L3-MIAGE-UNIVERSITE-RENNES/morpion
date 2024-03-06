package tro.dieng.morpion;

public enum Owner {
    NONE, FIRST, SECOND;
    public Owner opposite() {
    return this == SECOND ? FIRST : this == FIRST ? SECOND : NONE; }
}

