package github.visual4.aacweb.dictation.domain.user;

import java.util.HashMap;
import java.util.Map;

public enum Vendor {
    GOOGLE("GG"),
    NAVER("NV"),
    KAKAO("KK");

    private String code;
    private static Map<String, Vendor> map;
    static {
        map = new HashMap<>();
        for(Vendor v : Vendor.values()) {
            map.put(v.code, v);
        }
    }
    
    Vendor(String code) {
        this.code = code;
    }

    public String toCode() {
        return code;
    }
    
    public static Vendor fromCode(String code) {
        Vendor v = map.get(code);
        if (v == null) {
            throw new RuntimeException("invalid vendor code " + code);
        }
        return v;
    }
}
