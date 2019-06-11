package nz.ac.vuw.swen301.assignment3.server;

import java.util.Objects;

public class Pair {
    public String first;
    public String second;
    public Pair(String first, String second){
        this.first=first;
        this.second=second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public String getFirst(){
        return first;
    }

    public String getSecond(){
        return second;
    }
}
