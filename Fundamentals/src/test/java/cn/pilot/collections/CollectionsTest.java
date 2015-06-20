package cn.pilot.collections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.util.Comparator;
import java.util.TreeSet;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CollectionsTest {
    @Test
    public void treeSet_doesNOT_need_hashCode_and_equals() throws Exception {
        TreeSet<Custom> sort = new TreeSet<>();

        Custom c1 = new Custom(1);
        Custom c2 = new Custom(2);
        Custom c3 = new Custom(3);

        sort.add(c1);
        sort.add(c1);
        sort.add(c2);
        sort.add(c3);

        assertThat("size", sort.size(), equalTo(3));

        for (Custom c : sort) {
            assertThat("add():hashCode is called?", c.isHashCodeCalled(), equalTo(false));
            assertThat("add(): equals is called?", c.isEqualsCalled(), equalTo(false));
        }

        assertThat("contains(): hashCode is called?", c2.isHashCodeCalled(), equalTo(false));
        assertThat("contains(): equals is called?", c2.isEqualsCalled(), equalTo(false));

        assertThat("compareTo: negative means itself is 'smaller'", sort.first().getVar(), equalTo(1));
    }
}

@Getter
@Setter
class Custom implements Comparable<Custom> {
    private int var;

    private boolean isEqualsCalled;
    private boolean isHashCodeCalled;

    public Custom(int var) {
        this.var = var;
    }

    @Override
    public boolean equals(Object o) {
        isEqualsCalled = true;
        return true;
    }

    @Override
    public int hashCode() {
        isHashCodeCalled = true;
        return 1;
    }

    @Override
    public int compareTo(Custom o) {
        System.out.println("1");
        return var - o.var;
    }
}