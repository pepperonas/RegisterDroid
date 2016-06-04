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
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         Represent money data structure.
 */
public enum Tag {
    CENT_01(1), CENT_02(2), CENT_05(5),
    CENT_10(10), CENT_20(20), CENT_50(50),

    EURO_001(100), EURO_002(200), EURO_005(500),
    EURO_010(1000), EURO_020(2000), EURO_050(5000),
    EURO_100(10000), EURO_200(20000), EURO_500(50000);

    int v;


    Tag(int v) {
        this.v = v;
    }


    public int getValue() {
        return v;
    }


    /**
     * Show the tag's value.
     */
    @Override
    public String toString() {
        switch (valueOf(name())) {
            case CENT_01: return "0,01€";
            case CENT_02: return "0,02€";
            case CENT_05: return "0,05€";
            case CENT_10: return "0,10€";
            case CENT_20: return "0,20€";
            case CENT_50: return "0,50€";
            case EURO_001: return "1€";
            case EURO_002: return "2€";
            case EURO_005: return "5€";
            case EURO_010: return "10€";
            case EURO_020: return "20€";
            case EURO_050: return "50€";
            case EURO_100: return "100€";
            case EURO_200: return "200€";
            case EURO_500: return "500€";
            default: return "0€";
        }
    }
}
