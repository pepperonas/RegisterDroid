package com.pepperonas.registerdroid.model;

import com.pepperonas.registerdroid.various.Config;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         The app is able to manage three {@link Register} objects.
 *         This class represents the data structure.
 */
public class Register {

    private String linker;
    private Balance balance;


    public Register(String linker) {
        this.linker = linker;
        balance = new Balance(linker);
    }


    public String getLinker() {
        return linker;
    }


    public Balance getBalance() {
        return balance;
    }


    public void makeCollection(int... args) {
        int i = 0;
        for (Tag t : this.balance.getMoneyMap().keySet()) {
            this.balance.getMoneyMap().put(t, args[i++]);
        }

    }


    public static String getId(int id) {
        switch (id) {
            case 0: return Config.REGISTER_NAME_R1;
            case 1: return Config.REGISTER_NAME_R2;
            case 2: return Config.REGISTER_NAME_R3;
            default: return Config.REGISTER_NAME_R1;
        }
    }

}
