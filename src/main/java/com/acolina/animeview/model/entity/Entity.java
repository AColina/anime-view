package com.acolina.animeview.model.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Objects;


public abstract class Entity implements Serializable {

    @Id
    protected Integer _id;

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity entity = (Entity) o;
        return Objects.equals(_id, entity._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "_id=" + _id +
                '}';
    }
}
