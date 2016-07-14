package io.capman.common.conf;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by flying on 5/17/16.
 */
public class URIConfig {

    protected URI uri;

    private Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();

    public void mergeQuery(String q) throws UnsupportedEncodingException {
        if(q == null) return;
        final String[] pairs = q.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!params.containsKey(key)) {
                params.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            params.get(key).add(value);
        }
    }

    public URIConfig(String u)  {
        try {
            this.uri = new URI(u);
            mergeQuery(uri.getQuery());
        } catch (Exception e) {
            throw new IllegalArgumentException("URI Syntax Error: "+e.getMessage());
        }
    }

    public URI getURI() {
        return uri;
    }

    public boolean getParamBoolean(String key){
        List<String> strings = params.get(key);
        return Boolean.parseBoolean(strings.get(0));
    }
    public boolean getParamBoolean(String key, boolean defaultValue){
        List<String> strings = params.get(key);
        if(strings == null){
            return defaultValue;
        }
        return Boolean.parseBoolean(strings.get(0));
    }
    public int getParamInt(String key){
        List<String> strings = params.get(key);
        return Integer.parseInt(strings.get(0));
    }
    public int getParamInt(String key, int defaultValue){
        List<String> strings = params.get(key);
        if(strings == null){
            return defaultValue;
        }
        return Integer.parseInt(strings.get(0));
    }

    public double getParamDouble(String key){
        List<String> strings = params.get(key);
        return Double.parseDouble(strings.get(0));
    }
    public double getParamDouble(String key, double defaultValue){
        List<String> strings = params.get(key);
        if(strings == null){
            return defaultValue;
        }
        return Double.parseDouble(strings.get(0));
    }

    public long getParamLong(String key){
        List<String> strings = params.get(key);
        return Long.parseLong(strings.get(0));
    }
    public long getParamLong(String key, long defaultValue){
        List<String> strings = params.get(key);
        if(strings == null){
            return defaultValue;
        }
        return Long.parseLong(strings.get(0));
    }

    public String getParamString(String key){
        List<String> strings = params.get(key);
        return (strings.get(0));
    }
    public String getParamString(String key, String defaultValue){
        List<String> strings = params.get(key);
        if(strings == null){
            return defaultValue;
        }
        return (strings.get(0));
    }

    public int[] getParamIntArray(String key){
        List<String> strings = params.get(key);
        int[] rtn = new int[strings.size()];
        for (int i = 0; i < rtn.length; i++) {
            rtn[i] = Integer.parseInt(strings.get(i));
        }
        return rtn;
    }

    public double[] getParamDoubleArray(String key){
        List<String> strings = params.get(key);
        double[] rtn = new double[strings.size()];
        for (int i = 0; i < rtn.length; i++) {
            rtn[i] = Double.parseDouble(strings.get(i));
        }
        return rtn;
    }

    public long[] getParamLongArray(String key){
        List<String> strings = params.get(key);
        long[] rtn = new long[strings.size()];
        for (int i = 0; i < rtn.length; i++) {
            rtn[i] = Long.parseLong(strings.get(i));
        }
        return rtn;
    }

    public String[] getParamStringArray(String key){
        List<String> strings = params.get(key);
        return strings.toArray(new String[]{});
    }

}
