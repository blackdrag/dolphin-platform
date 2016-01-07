package com.canoo.dolphin.qualifier;

/**
 * Created by hendrikebbers on 07.01.16.
 */
public class Qualifier<T> {

    private String name;

    public Qualifier(String name) {
        if(name == null) {
            throw new IllegalArgumentException("name must not be null!");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qualifier<?> qualifier = (Qualifier<?>) o;

        return name.equals(qualifier.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
