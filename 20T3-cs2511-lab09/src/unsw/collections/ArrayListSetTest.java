package unsw.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import unsw.fruit.Apple;
import unsw.fruit.Fruit;
import unsw.fruit.Orange;

class ArrayListSetTest {

    @Test
    void testBasics() {
        Set<String> set = new ArrayListSet<>();
        set.add("Hello");
        set.add("World");
        assertTrue(set.contains("Hello"));
        assertTrue(set.contains("World"));

        set.remove("Hello");
        assertFalse(set.contains("Hello"));
        assertTrue(set.contains("World"));
    }

    @Test
    void testSubsetOf() {
        Set<Fruit> fruit = new ArrayListSet<Fruit>();
        fruit.add(new Apple("Gala"));
        fruit.add(new Apple("Fuji"));
        fruit.add(new Orange("Navel"));

        Set<Apple> apples = new ArrayListSet<>();
        apples.add(new Apple("Gala"));
        apples.add(new Apple("Fuji"));

        assertTrue(apples.subsetOf(fruit));
        assertFalse(fruit.subsetOf(apples));

        fruit.remove(new Apple("Fuji"));

        assertFalse(apples.subsetOf(fruit));
    }

    @Test
    void testUnion() {
        Set<Fruit> apple1 = new ArrayListSet<Fruit>();
        Set<Fruit> apple2 = new ArrayListSet<Fruit>();
        Set<Fruit> apple3 = apple1.union(apple2);

        assertEquals(0, apple3.size());

        Apple a1 = new Apple("a1");
        Apple a2 = new Apple("a2");
        Apple a3 = new Apple("a3");

        apple1.add(a1);
        apple2.add(a2);
        apple2.add(a3);

        apple3 = apple1.union(apple2);
        assertEquals(3, apple3.size());
        apple3 = apple1.union(apple2);
        assertEquals(3, apple3.size());

    }

    @Test
    void testIntersection() {
        Set<Fruit> orange1 = new ArrayListSet<Fruit>();
        Set<Fruit> orange2 = new ArrayListSet<Fruit>();
        Set<Fruit> orange3 = orange1.intersection(orange2);

        assertEquals(0, orange3.size());

        Orange a1 = new Orange("a1");
        Orange a2 = new Orange("a2");
        Orange a3 = new Orange("a3");

        orange1.add(a1);
        orange2.add(a1);
        orange2.add(a2);
        orange2.add(a3);

        orange3 = orange1.intersection(orange2);

        assertEquals(1, orange3.size());

        orange1.remove(a1);
        orange2.remove(a1);
        orange2.remove(a2);
        orange2.remove(a3);
        orange3.remove(a1);

        assertEquals(0, orange1.size());
        assertEquals(0, orange2.size());
        assertEquals(0, orange3.size());

        orange1.add(a1);
        orange2.add(a2);

        orange3 = orange1.intersection(orange2);

        assertEquals(0, orange3.size());
    }

    @Test
    void testEqual() {
        Set<Fruit> apple1 = new ArrayListSet<Fruit>();
        Set<Fruit> apple2 = new ArrayListSet<Fruit>();


        Apple a1 = new Apple("a1");
        Apple a2 = new Apple("a2");
        Apple a3 = new Apple("a3");

        apple1.add(a1);
        apple2.add(a1);

        assertEquals(apple1, apple2);

        apple2.add(a2);
        assertEquals(apple1 == apple2, false);

        apple1.add(a3);

        assertEquals(apple1 == apple2, false);
    }
}
