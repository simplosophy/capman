package io.capman.conf;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;

import java.util.List;

/**
 * Created by flying on 6/7/16.
 */
public class JSConfigUtils {

    public static int value2int(Object value) throws JSConfigFormatException {
        if(value instanceof Number){
            return ( (Number) value).intValue();
        }
        else if (value instanceof String){
            return Integer.parseInt(value.toString());
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }

    public static int[] value2intArray(Object o) throws JSConfigFormatException {
        if(o instanceof List){
            List ls = (List) o;
            int[] rtn = new int[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = value2int(ls.get(i));
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }

    private static final long maxIntegerInDouble = 2L << 53;

    public static long value2long(Object value) throws JSConfigFormatException, JSConfigPrecisionException {
        if(value instanceof Number){
            Number d = (Number) value;
            if(d.doubleValue() > maxIntegerInDouble){
                throw new JSConfigPrecisionException("Reach Max Long Value That Can be Stored In double, Try Use String rather than Double " );
            }
            return d.longValue();
        }
        else if (value instanceof String){
            return Long.parseLong(value.toString());
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }
    public static long[] value2longArray(Object o) throws JSConfigFormatException, JSConfigPrecisionException {
        if(o instanceof List){
            List ls = (List) o;
            long[] rtn = new long[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = value2long(ls.get(i));
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }

    public static double value2double(Object value) throws JSConfigFormatException, JSConfigPrecisionException {
        if(value instanceof Number){
            Number d = (Number) value;
            return d.doubleValue();
        }
        else if (value instanceof String){
            return Double.parseDouble(value.toString());
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }
    public static double[] value2doubleArray(Object o) throws JSConfigFormatException, JSConfigPrecisionException {
        if(o instanceof List){
            List ls = (List) o;
            double[] rtn = new double[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = value2double(ls.get(i));
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }

    public static float value2float(Object value) throws JSConfigFormatException, JSConfigPrecisionException {
        if(value instanceof Number){
            Number f = (Number) value;
            return f.floatValue();
        }
        else if (value instanceof String){
            return Float.parseFloat(value.toString());
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }
    public static float[] value2floatArray(Object o) throws JSConfigFormatException, JSConfigPrecisionException {
        if(o instanceof List){
            List ls = (List) o;
            float[] rtn = new float[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = value2float(ls.get(i));
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }

    public static boolean value2bool(Object value) throws JSConfigFormatException, JSConfigPrecisionException {
        if(value instanceof Number) {
            int v =  ((Number) value).intValue();
            if(v == 0){
                return false;
            }else {
                return true;
            }
        }else if(value instanceof Boolean){
            return ((Boolean)value).booleanValue();
        }else  if(value instanceof String){
            String v = ((String) value).toLowerCase();
            if(v.equals("true")){
                return true;
            }else {
                return false;
            }
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }
    public static boolean[] value2boolArray(Object o) throws JSConfigFormatException, JSConfigPrecisionException {
        if(o instanceof List){
            List ls = (List) o;
            boolean[] rtn = new boolean[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = value2bool(ls.get(i));
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }
    public static String[] value2StringArray(Object o) throws JSConfigFormatException, JSConfigPrecisionException {
        if(o instanceof List){
            List ls = (List) o;
            String[] rtn = new String[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = (ls.get(i).toString());
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }

    public static ByteString value2bytes(Object value) throws JSConfigFormatException, JSConfigPrecisionException {
        if(value instanceof String){
            return ByteString.copyFromUtf8((value).toString());
        }else if(value instanceof List){
            List vLs = (List)value;
            ByteString.Output output = ByteString.newOutput(vLs.size());
            for (Object bb : vLs) {
                try {
                    int i = Integer.parseInt(bb.toString());
                    output.write(i);
                }catch (Exception e){
                    throw new JSConfigFormatException("Invalid Byte In Array, Type Miss Match Field ");
                }
            }
            return output.toByteString();
        }else {
            throw new JSConfigFormatException("Type Miss Match Field "  );
        }
    }
    public static ByteString[] value2bytesArray(Object o) throws JSConfigFormatException, JSConfigPrecisionException {
        if(o instanceof List){
            List ls = (List) o;
            ByteString[] rtn = new ByteString[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = value2bytes(ls.get(i));
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }

    public static Descriptors.EnumValueDescriptor value2enum(Object value, Descriptors.EnumDescriptor enumType) throws JSConfigFormatException, JSConfigPrecisionException {
        if(value instanceof Number){
            int i =  ((Number) value).intValue();
            Descriptors.EnumValueDescriptor valueByNumber = enumType.findValueByNumber(i);
            if(valueByNumber == null){
                throw new JSConfigFormatException("Cannot Find Enum By Num "+value+" Field: "  );
            }
            return valueByNumber;
        }else if(value instanceof String){
            Descriptors.EnumValueDescriptor valueByString = enumType.findValueByName(value.toString());
            if(valueByString == null){
                throw new JSConfigFormatException("Cannot Find Enum By Name "+value+" Field: "  );
            }
            return valueByString;
        }
        else {
            throw new JSConfigFormatException("Type Miss Match Field "  );
        }
    }


    public static Descriptors.EnumValueDescriptor[] value2enumArray(Object o, Descriptors.EnumDescriptor enumType) throws JSConfigFormatException, JSConfigPrecisionException {
        if(o instanceof List){
            List ls = (List) o;
            Descriptors.EnumValueDescriptor[] rtn = new Descriptors.EnumValueDescriptor[ls.size()];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = value2enum(ls.get(i), enumType);
            }
            return rtn;
        }else {
            throw new JSConfigFormatException("Type Miss Match Field " );
        }
    }


}
