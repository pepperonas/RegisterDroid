/*
 * Copyright (c) 2016 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
