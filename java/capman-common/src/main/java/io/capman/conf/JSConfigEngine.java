package io.capman.conf;


import com.google.protobuf.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.v8dtoa.DoubleConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flying on 6/7/16.
 */
public class JSConfigEngine {
    private static Logger LOGGER = LoggerFactory.getLogger(JSConfigEngine.class);

    private JSConfigEngine(){}

    private static final JSConfigEngine instance = new JSConfigEngine();

    public static JSConfigEngine getInstance() {
        return instance;
    }

    Context context;
    Scriptable scope;

    public void init(){
        context = Context.enter();
        scope = context.initSafeStandardObjects();
    }

    public Object eval(String jsStr){
        Object obj = context.evaluateString(scope, jsStr, null, 1, null);
        return obj;
    }

    private Map<Descriptors.Descriptor, Class> classNameCache = new HashMap<Descriptors.Descriptor, Class>();

    private Class getClassFullNameForMessageDescriptor(Descriptors.Descriptor messageType) throws ClassNotFoundException {
        if(classNameCache.containsKey(messageType)){
            return classNameCache.get(messageType);
        }
        String fullName = messageType.getFullName();
        DescriptorProtos.FileOptions fileOptions = messageType.getFile().getOptions();
        String outerClassname = fileOptions.getJavaOuterClassname();
        if("".equals(outerClassname)){
            String pbfile = messageType.getFile().getFullName();
            int i = pbfile.lastIndexOf('/');
            if(i <0 )i = 0;
            String filenamepart = pbfile.substring(i+1, pbfile.length() - 6);//取文件名
            outerClassname = Character.toUpperCase(filenamepart.toCharArray()[0]) + filenamepart.substring(1);
        }
        int idx = fullName.lastIndexOf('.');
        Class rtn = Class.forName(String.format("%s.%s$%s", fullName.substring(0, idx), outerClassname, fullName.substring(idx + 1)));
        classNameCache.put(messageType, rtn);
        return rtn;
    }

    private Map<Class, Method> builderMethodCache = new HashMap<Class, Method>();
    private Object newBuilderForMsgDescripor(Descriptors.Descriptor messageType) throws JSConfigFormatException {
        try {
            Class clz = null;
            clz = getClassFullNameForMessageDescriptor(messageType);
            if(!builderMethodCache.containsKey(clz)){
                Method m = clz.getMethod("newBuilder");
                builderMethodCache.put(clz, m);
            }
            return builderMethodCache.get(clz).invoke(clz);
        } catch (Exception e) {
            throw new JSConfigFormatException("Create New Builder Failed.", e);
        }
    }


    /**
     * 将JavaScript对象转换为protobuf的Message
     */
    private <Builder extends GeneratedMessage.Builder>
    GeneratedMessage.Builder<Builder> js2pb(Map m, GeneratedMessage.Builder<Builder> b) throws JSConfigFormatException {
        Descriptors.Descriptor descriptor = b.getDescriptorForType();

        for (Object o : m.keySet()) {
            String key = o.toString();
            Object value = m.get(o);

            Descriptors.FieldDescriptor field = descriptor.findFieldByName(key);

            if(field != null){

                try{
                    switch (field.getType()){
                        case INT32:
                        case SINT32:
                        case SFIXED32:
                        case UINT32:
                        case FIXED32:
                            if(field.isRepeated()){
                                for (int i : JSConfigUtils.value2intArray(value)) {
                                    b.addRepeatedField(field, i);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2int(value));
                            }
                            break;

                        case INT64:
                        case SINT64:
                        case SFIXED64:
                        case UINT64:
                        case FIXED64:
                            if(field.isRepeated()){
                                for (long l : JSConfigUtils.value2longArray(value)) {
                                    b.addRepeatedField(field, l);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2long(value));
                            }
                            break;

                        case FLOAT:
                            if(field.isRepeated()){
                                for (float v : JSConfigUtils.value2floatArray(value)) {
                                    b.addRepeatedField(field, v);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2float(value));
                            }
                            break;
                        case DOUBLE:
                            if(field.isRepeated()){
                                for (double v : JSConfigUtils.value2doubleArray(value)) {
                                    b.addRepeatedField(field, v);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2double(value));
                            }
                            break;

                        case BOOL:
                            if(field.isRepeated()){
                                for (boolean b1 : JSConfigUtils.value2boolArray(value)) {
                                    b.addRepeatedField(field, b1);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2bool(value));
                            }
                            break;

                        case STRING:
                            if(field.isRepeated()){
                                for (String s : JSConfigUtils.value2StringArray(value)) {
                                    b.addRepeatedField(field, s);
                                }
                            }else {
                                b.setField(field, String.valueOf(value));
                            }
                            break;

                        case BYTES:
                            if(field.isRepeated()){
                                for (ByteString bytes : JSConfigUtils.value2bytesArray(value)) {
                                    b.addRepeatedField(field, bytes);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2bytes(value));
                            }
                            break;

                        case ENUM:
                            if(field.isRepeated()){
                                for (Descriptors.EnumValueDescriptor enumValueDescriptor : JSConfigUtils.value2enumArray(value, field.getEnumType())) {
                                    b.addRepeatedField(field, enumValueDescriptor);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2enum(value, field.getEnumType()));
                            }
                            break;

                        case MESSAGE:
                        case GROUP:
                            Descriptors.Descriptor messageType = field.getMessageType();

                            if(field.isRepeated()){
                                if(value instanceof List){
                                    List ls = (List) value;
                                    for (Object l : ls) {
                                        if(l instanceof Map){
                                            //递归
                                            Object builder = newBuilderForMsgDescripor(messageType);
                                            GeneratedMessage.Builder<Builder> bb = js2pb((Map) l, (GeneratedMessage.Builder<Builder>) builder);
                                            b.addRepeatedField(field, bb.buildPartial());
                                        }else {
                                            throw new JSConfigFormatException("Type Miss Match Field, Require Map. " );
                                        }
                                    }
                                }else {
                                    throw new JSConfigFormatException("Type Miss Match Field, Require List. " );
                                }

                            }else {
                                if(value instanceof Map){
                                    //递归
                                    Object builder = newBuilderForMsgDescripor(messageType);
                                    GeneratedMessage.Builder<Builder> bb = js2pb((Map) value, (GeneratedMessage.Builder<Builder>) builder);
                                    b.setField(field, bb.buildPartial());

                                }else {
                                    throw new JSConfigFormatException("Type Miss Match Field, Require Map. " );
                                }

                            }

                            break;

                        default:
                            throw new RuntimeException("Should Not Come Here.");
                    }
                }catch (JSConfigPrecisionException e){
                    LOGGER.warn(e.getMessage() + field.getFullName());
                }catch (JSConfigFormatException e){
                    throw new JSConfigFormatException(e.getMessage() + field.getFullName());
                }
            }
        }

        return b;
    }


    /**
     * JavaScript对象转换为ProtoBuf的Message
     * @param jsStr
     * @param pbClass
     * @param <T> 必须是带有descriptor的Message(OptimizeMode不能是optimize_for = LITE_RUNTIME)
     * @return
     * @throws JSConfigFormatException
     */
    public <T extends AbstractMessage> T js2PbMsg(String jsStr, Class<T> pbClass) throws JSConfigFormatException {
        try {
            GeneratedMessage.Builder<GeneratedMessage.Builder> builder = null;
            builder = (GeneratedMessage.Builder<GeneratedMessage.Builder>) pbClass.getMethod("newBuilder").invoke(pbClass);
            Map map = (Map) eval(jsStr);
            GeneratedMessage.Builder<GeneratedMessage.Builder> bb = js2pb(map, builder);
            return (T) bb.build();
        } catch (Exception e) {
            throw new JSConfigFormatException(e.getMessage());
        }
    }

    public void close(){
        Context.exit();
    }

}
