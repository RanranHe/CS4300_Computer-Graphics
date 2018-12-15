//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package util;

import java.util.HashMap;
import java.util.Map;

public class ShaderLocationsVault {
    private Map<String, Integer> vars = new HashMap();

    public ShaderLocationsVault() {
    }

    public void add(String var, Integer location) {
        this.vars.put(var, location);
    }

    public Integer getLocation(String var) {
        return this.vars.containsKey(var)?(Integer)this.vars.get(var):Integer.valueOf(-1);
    }
}
