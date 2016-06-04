package com.pepperonas.registerdroid.model;

/**
 * @author Martin Pfeffer, Sebastian Grätz)
 *         <p/>
 *         Accessor to collect a {@link Balance}
 *         and the total amount of money.
 */
public class BalanceResult extends Balance {

    private int total;


    public BalanceResult(Balance balance, int total) {
        super(balance.getLinker());
        setMoneyMap(balance.getMoneyMap());
        this.total = total;
    }


    public int getTotal() {
        return total;
    }

}
