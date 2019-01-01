package OOP.Solution;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class OOPBackup {
    private static OOPBackup ourInstance = new OOPBackup();

    private HashMap<String, HashMap<String, Object>> backup;

    public static OOPBackup getInstance() {
        return ourInstance;
    }

    public void backup(Object instance, String backup_name) {
        if (instance == null || backup_name.isEmpty()) {
            return;
        }
        Field[] fields = instance.getClass().getDeclaredFields();
        HashMap<String, Object> subBackup = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(instance);
                Object backup = getClonedInstance(value);
                if (backup != null) {
                    subBackup.put(field.getName(), backup);
                } else if ((backup = getCopyInstance(value)) != null) {
                    subBackup.put(field.getName(), backup);
                } else {
                    subBackup.put(field.getName(), value);
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        backup.put(backup_name, subBackup);
    }

    public void recover(Object instance, String backup_name) {
        if (instance == null || backup_name.isEmpty()) return;
        HashMap<String, Object> backup = this.backup.get(backup_name);
        backup.forEach((fieldName, copy) -> {
            try {
                Field field = instance.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(instance, copy);
            } catch (NoSuchFieldException | IllegalAccessException ignore) {
            }

        });
    }

    private OOPBackup() {
        backup = new HashMap<>();
    }

    private Object getClonedInstance(Object instance) {
        if (instance == null) return null;
        if (instance instanceof Cloneable) { //supports clone
            try {
                return instance.getClass().getMethod("clone").invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignore) {
            }
        }
        return null;
    }

    //tries to use copy constructor.
    private Object getCopyInstance(Object instance) {
        if (instance == null) return null;
        try {
            return instance.getClass().getConstructor(instance.getClass()).newInstance(instance);
        } catch (NoSuchMethodException | InvocationTargetException
                | InstantiationException | IllegalAccessException ignore) {

        }
        return null;
    }
}
