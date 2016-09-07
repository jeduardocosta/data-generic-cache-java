package datagenericcache.factories;

public class InstanceFactory {
    public <T> T create(Class type) {
        T instance = null;

        try {
            return (T)type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
        }

        return instance;
    }
}