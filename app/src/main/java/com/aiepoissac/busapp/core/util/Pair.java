package com.aiepoissac.busapp.core.util;

public final class Pair<H, T> {

    private final H head;

    private final T tail;

    public Pair(H head, T tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public int hashCode() {
        return head.hashCode() + tail.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair<?, ?> that) {
            return this.head.equals(that.head) && this.tail.equals(that.tail);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", this.head, this.tail);
    }

}
