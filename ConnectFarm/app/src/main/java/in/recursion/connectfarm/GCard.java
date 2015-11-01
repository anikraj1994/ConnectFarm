package in.recursion.connectfarm;

/**
 * Created by anikr on 8/16/2015.
 */
public class GCard {
    private int id;
    private int sum;
    private int type;

    public GCard(int id, int sum, int type) {
        this.id = id;
        this.sum = sum;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
