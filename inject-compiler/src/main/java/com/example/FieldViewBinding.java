package com.example;

import javax.lang.model.type.TypeMirror;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class FieldViewBinding {
    private String name;//  textview
    private TypeMirror type ;//--->TextView
    private int resId;//--->R.id.textiew

    public FieldViewBinding(String name, TypeMirror type, int resId) {
        this.name = name;
        this.type = type;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public TypeMirror getType() {
        return type;
    }

    public int getResId() {
        return resId;
    }
}
