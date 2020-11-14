package jmp;

public class JMPPlayer {
    public static void main(String[] args) {
        int res = (JMPLoader.invoke(args) == true) ? 0 : 1;
        System.exit(res);
    }
}
