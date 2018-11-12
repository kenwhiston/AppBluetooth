package pe.edu.bitec.appbluetooth;

import java.io.Serializable;

public class ItemBluetooth implements Serializable {

    public String name;
    public String number;

    public ItemBluetooth() {
    }

    public ItemBluetooth(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
