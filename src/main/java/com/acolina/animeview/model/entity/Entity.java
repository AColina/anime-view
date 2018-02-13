package com.acolina.animeview.model.entity;

import java.io.Serializable;

public abstract class Entity implements Serializable {

    protected Integer _id;

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }
}
