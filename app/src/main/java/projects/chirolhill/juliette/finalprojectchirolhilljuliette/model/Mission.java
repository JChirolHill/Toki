package projects.chirolhill.juliette.finalprojectchirolhilljuliette.model;

import java.util.Random;

public class Mission {
    private String type;
    private String content;
    private static Random r;

    public Mission() {
    }

    public Mission(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // returns randomly generated num between 1 and max
    public static int getNum(int max) {
        r = new Random();
        return r.nextInt(max - 1) + 1;
    }

//    public void setNum(int num) {
//        this.num = num;
//    }
}
