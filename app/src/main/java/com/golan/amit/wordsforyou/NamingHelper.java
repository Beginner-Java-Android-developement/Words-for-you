package com.golan.amit.wordsforyou;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class NamingHelper {

    public static final String[] NAMESTR = {
            "SEVEN", "TABLE", "HORSE", "CHAIR", "EIGHT", "PAINT", "WORDS", "STEAK", "STICK",
            "NAMES", "ARIEL", "LIORI", "GOLAN", "SNAKE", "SNAIL", "PAUSE", "SWEET",
            "FRUIT", "APPLE", "TREES", "HONEY", "PHONE", "BOOKS", "WHEAT", "SWIPE", "CLEAN",
            "DIRTY", "WATER", "BREAD", "SUGAR", "KNIFE", "KNITE", "NIGHT", "SPOON", "EARTH",
            "FENCE", "WRITE", "TRAIN", "PLAIN", "RIGHT", "FIGHT", "PEARL", "BENCH", "COUCH",
            "GLASS", "HUMAN", "GRASS", "THINK", "WHITE", "BLACK", "GREEN", "BOARD", "THINK",
            "DIGIT", "COUNT", "FLOOR", "SPEAK", "DREAM", "STORY", "BREAK", "HEART", "SHOES",
            "LEAVE", "SWEAT", "WASTE", "PAPER", "NOISE", "SOUND", "MOTOR", "EVERY", "NEVER",
            "NURSE", "MOUSE", "SCALE", "PIANO", "BRICK", "STONE", "BARBI", "TOOTH", "CLOUD",
            "SPACE"
    };

    public static final String[] TRANSLATEHEBSTR = {
            "שבע", "שולחן", "סוס", "כסא", "שמונה", "ציור", "מילים", "אומצה", "מקל",
            "שמות", "אריאל", "ליאורי", "גולן", "נחש", "חילזון", "הפסקה", "מתוק",
            "פרי", "תפוח", "עצים", "דבש", "טלפון", "ספרים", "חיטה", "הזזה", "נקי",
            "מלוכלך", "מים", "לחם", "סוכר", "סכין", "אביר", "לילה", "כפית", "כדור הארץ",
            "גדר", "לכתוב", "רכבת", "מטוס", "נכון / ימין", "קרב", "פנינה", "ספסל", "ספה / כורסה",
            "זכוכית", "אנושי", "דשא / עשב", "לחשוב", "לבן", "שחור", "ירוק", "לוח / קרש", "לחשוב",
            "סיפרה", "ספירה", "רצפה", "לדבר", "לחלום", "סיפור", "לשבור / הפסקה", "לב", "נעליים",
            "לעזוב", "זיעה", "בזבוז / אשפה", "נייר", "רעש", "צליל / קול", "מנוע", "כל", "לעולם לא",
            "אחות", "עכבר", "משקל", "פסנתר", "לבנה", "אבן", "בובת ברבי", "שן", "ענן",
            "רווח / חלל"
    };

    public static final String[] alternativeWords = {
            "SWORD", "MEANS", "SHORE", "SNEAK", "LOGAN", "WASTE", "SWEAT",
            "HEART", "EARTH", "SKATE", "BROAD"
    };
    private int name_ptr;

    private int[] rnd_name;
    /**
     * Position handeling
     */
    private int name_counter;
    private Stack<Integer> sti, stu;

    public NamingHelper() {
        generate_rnd_ptr_name();
        rnd_name = new int[NAMESTR[name_ptr].length()];
        resetName_counter();
        sti = new Stack<>();
        stu = new Stack<>();
    }

    public boolean isAlternativeWordExist(String word) {
//        return Arrays.stream(alternativeWords).anyMatch(word::equals);
        List<String> list = Arrays.asList(alternativeWords);
        return list.contains(word);
    }

    public String getNameCharByIndex(int ind) {
        if (ind < 0 || ind > rnd_name.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(NAMESTR[name_ptr].charAt(rnd_name[ind]));
        return sb.toString();
    }

    public String name_rnd_representation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rnd_name.length; i++) {
            sb.append(NAMESTR[name_ptr].charAt(rnd_name[i]));
            if (i < (rnd_name.length - 1)) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public String getCurr_name() {
        return NAMESTR[name_ptr];
    }

    public String getCurr_heb_name() {
        return TRANSLATEHEBSTR[name_ptr];
    }

    /**
     * Random generator
     */
    private void generate_rnd_ptr_name() {
        this.name_ptr = (int) (Math.random() * NAMESTR.length);
    }

    public void generate_random_name() {
        List<Integer> computerNumberList = new ArrayList<>();
        for (int i = 0; i < NAMESTR[name_ptr].length(); i++) {
            computerNumberList.add(i);
        }
        Collections.shuffle(computerNumberList);
        for (int i = 0; i < NAMESTR[name_ptr].length(); i++) {
            rnd_name[i] = computerNumberList.get(i);
        }
    }

    public void generate_ordered_name() {
        List<Integer> computerNumberList = new ArrayList<>();
        for (int i = 0; i < NAMESTR[name_ptr].length(); i++) {
            computerNumberList.add(i);
        }
        for (int i = 0; i < NAMESTR[name_ptr].length(); i++) {
            rnd_name[i] = computerNumberList.get(i);
        }
    }

    public int[] getRnd_name() {
        return rnd_name;
    }

    /**
     * Position handeling
     */

    private void resetName_counter() {
        this.name_counter = 0;
    }

    public int getName_counter() {
        return name_counter;
    }

    public void increaseName_counter() {
        this.name_counter++;
    }

    public void decreaseName_counter() {
        this.name_counter--;
    }

    /**
     * Stack handling
     *
     * @return
     */

    public Stack<Integer> getSti() {
        return sti;
    }

    public Stack<Integer> getStu() {
        return stu;
    }

    public void setSti(Stack<Integer> sti) {
        this.sti = sti;
    }

    public void push_stack(Integer item) {
        this.sti.push(item);
    }

    public void push_stack_u(Integer item) {
        this.stu.push(item);
    }

    public Integer pop_stack() {
        if (sti.size() > 0)
            return this.sti.pop();
        else
            return -1;
    }

    public Integer pop_stack_u() {
        if (stu.size() > 0)
            return this.stu.pop();
        else
            return -1;
    }

    /**
     * Getters & Setters
     */
    public int getName_ptr() {
        return name_ptr;
    }

    public void setName_ptr(int name_ptr) {
        this.name_ptr = name_ptr;
    }

}
