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

import java.util.TreeMap;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         The data structure of a balance.
 */
public class Balance {

    public static int AMOUNT_VALUES = 15;

    private final String linker;

    private TreeMap<Tag, Integer> moneyMap = new TreeMap<Tag, Integer>();


    public Balance(String linker) {
        this.linker = linker;

        moneyMap.put(Tag.CENT_01, 0);
        moneyMap.put(Tag.CENT_02, 0);
        moneyMap.put(Tag.CENT_05, 0);
        moneyMap.put(Tag.CENT_10, 0);
        moneyMap.put(Tag.CENT_20, 0);
        moneyMap.put(Tag.CENT_50, 0);
        moneyMap.put(Tag.EURO_001, 0);
        moneyMap.put(Tag.EURO_002, 0);
        moneyMap.put(Tag.EURO_005, 0);
        moneyMap.put(Tag.EURO_010, 0);
        moneyMap.put(Tag.EURO_020, 0);
        moneyMap.put(Tag.EURO_050, 0);
        moneyMap.put(Tag.EURO_100, 0);
        moneyMap.put(Tag.EURO_200, 0);
        moneyMap.put(Tag.EURO_500, 0);

    }


    public void makeBalance(int... args) {
        int i = 0;
        for (Tag t : moneyMap.keySet()) moneyMap.put(t, args[i++]);
    }


    public String getLinker() { return linker; }


    public TreeMap<Tag, Integer> getMoneyMap() { return moneyMap; }


    public void setMoneyMap(TreeMap<Tag, Integer> moneyMap) { this.moneyMap = moneyMap; }

}
