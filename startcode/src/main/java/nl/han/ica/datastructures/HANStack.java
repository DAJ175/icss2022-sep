package nl.han.ica.datastructures;

import java.util.LinkedList;
import java.util.List;

public class HANStack<T> implements IHANStack<T>{
    List<T> Stack = new LinkedList<>();

    @Override
    public void push(T value) {
        Stack.add(value);
    }

    @Override
    public T pop() {
        return Stack.remove(Stack.size()-1);
    }

    @Override
    public T peek() {
        return Stack.get(Stack.size()-1);
    }
}
