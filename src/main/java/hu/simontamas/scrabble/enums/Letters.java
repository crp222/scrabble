package hu.simontamas.scrabble.enums;

public enum Letters {
    A(1),
    //Á(4),
    B(3),
    C(5),
    //CS(7),
    D(2),
    E(1),
    //É(4),
    F(4),
    G(3),
    //GY(8),
    H(4),
    I(1),
    //Í(4),
    J(8),
    K(5),
    L(1),
    //LY(8),
    M(3),
    N(1),
    //NY(8),
    O(1),
    //Ó(4),
    //Ö(7),
    //Ő(7),
    P(5),
    Q(10),
    R(1),
    S(1),
    //SZ(5),
    T(1),
    //TY(10),
    U(1),
    //Ú(4),
    //Ü(7),
    //Ű(7),
    V(4),
    W(10),
    X(10),
    Y(8),
    //ZS(7),
    Z(5),

    __(0);

    public final int value;

    Letters(int value) {
        this.value = value;
    }
}