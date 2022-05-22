package github.visual4.aacweb.dictation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TypeMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 4240257637229945922L;
    
    public TypeMap() {}
    public TypeMap(int capacity) {
        super(capacity);
    }
    public TypeMap(Map<? extends String, ? extends Object> src) {
        super(src);
    }
    public TypeMap(int capacity, float loadFactor) {
        super(capacity, loadFactor);
    }
    public Integer getInt(String key) {
        return get(Integer.class, key);
    }
    public Integer asInt(String key) {
        Object v = this.get(key);
        if (v instanceof Integer) {
            return (Integer) v;
        } else {
            return Integer.parseInt(v.toString());
        }
    }
    public Integer asInt(String key, Integer defaultValue) {
        Object v = this.get(key);
        if (v instanceof Integer) {
            return (Integer) v;
        } else {
            try {
                return Integer.parseInt(v.toString());                
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }
    public <T, R> List<R> asList(String key, Function<T, R> converter) {
        List<T> src = this.get(key);
        List<R> dst = new ArrayList<>();
        for (T t : src) {
            dst.add(converter.apply(t));
        }
        return dst;
    }
    public <T> List<T> asList(String key) {
        return this.get(key);
    }
    public Long getLong(String key) {
        return get(Long.class, key);
    }
    public Long asLong(String key) {
        Object v = this.get(key);
        if(v instanceof Long) {
            return (Long)v;
        } else {
            return Long.parseLong(v.toString());
        }
    }
    public Double getDouble(String key) {
        return get(Double.class, key);
    }
    public Double asDouble(String key) {
        Object v = this.get(key);
        if(v instanceof Double) {
            return (Double) v;
        } else {
            return Double.parseDouble(v.toString());
        }
    }
    public String getStr(String key) {
        return get(String.class, key);
    }
    public String asStr(String key) {
        Object v = this.get(key);
        return v == null ? null : v.toString();
    }
    private <T> T get(Class<T> cls, String key) {
        Object v = super.get(key);
        try {
            return cls.cast(v);
        } catch (ClassCastException e) {
            return cls.cast(exception("not a type of %s: %s(real type: %s)",
                    cls.getName(),
                    v.toString(),
                    v.getClass().getName()));            
        }
    }
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object v = super.get(key);
        try {
            return (T) v;
        } catch (ClassCastException e) {
            return (T) exception("not a expected type: %s(real type: %s)",
                    v.toString(),
                    v.getClass().getName());            
        }
    }
    private Object exception(String format, Object ... args) {
        throw new RuntimeException(String.format(format, args));
    }
    public static TypeMap with(String key, String value) {
        TypeMap m = new TypeMap(1);
        m.put(key, value);
        return m;
    }
    public static TypeMap copy(TypeMap src) {
    	return new TypeMap(src);
    }
    public static TypeMap with(Object ... args) {
        if (args.length % 2 == 1) {
            throw new AppException("SERVER_ERROR", 500, "args [(key,value), ...] expected");
        }
        TypeMap m = new TypeMap(args.length/2);
        for (int i = 0; i < args.length; i+=2) {
            Object k = args[i];
            Object v = args[i+1];
            if(k.getClass() != String.class) {
                throw new AppException(
                        "SERVER_ERROR",
                        500,
                        "key %s is not type of string but %s", k, k.getClass().getName());
            }
            m.put((String)k, v);
        }
        return m;
    }
    public <T, U> U validate(String key, Function<T, U> validator) {
        T value = get(key);
        return validator.apply(value);
    }
    public <T, U> Map<T, U> toMap() {
        return (Map<T, U>) this;
    }
    /**
     * value는 유지하고 대응하는 key를 바꿈
     * <pre>
     * [prevKey, v] => [newKey, v]
     * </pre>
     * @param prevKey
     * @param newKey
     */
    public void replaceKey(String prevKey, String newKey) {
        if (this.containsKey(newKey)) {
            throw new IllegalArgumentException("duplicated key " + newKey);
        }
        Object value = this.get(prevKey);
        if (value != null) {
            this.put(newKey, value);
            this.remove(prevKey);
        }
        
    }
    
	public boolean is(String prop, Object val) {
		Object v = this.get(prop);
		return v != null && v.equals(val);
	}
	public LocalDate getLocalDate(String prop) {
		String value = this.getStr(prop);
		if (value.length() > 10) {
			value = value.substring(0, 10);
		}
		try {
			return LocalDate.parse(value);
			
		} catch(DateTimeParseException e) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
	}
	public Boolean getBoolean(String prop) {
		Object v = this.get(prop);
		if (v.getClass() == Boolean.class) {
			return Boolean.class.cast(v);
		}
		return null;
	}
}
