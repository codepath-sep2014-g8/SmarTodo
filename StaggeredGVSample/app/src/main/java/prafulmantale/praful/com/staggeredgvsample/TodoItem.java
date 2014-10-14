package prafulmantale.praful.com.staggeredgvsample;

import java.io.Serializable;

/**
 * Created by prafulmantale on 10/13/14.
 */
public class TodoItem implements Serializable {

    private String item;

    public TodoItem() {
    }

    public TodoItem(String item) {
        this.item = item;
    }


    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "item='" + item + '\'' +
                '}';
    }
}
